# [Fetching from the server](http://facebook.github.io/react/docs/tutorial.html#fetching-from-the-server) (tutorials 11-14)

Up to this point, the comments in our list have been hard-coded into
our components. This would all be well and good if data magically
appeared in our programs when and where we wanted it to --
unfortunately neither computer programs or data possess magical
powers.

What we need now is to fetch data from a server. We'll start by
pulling the comments out of our application code and defining them in
a JSON file at the root of our project.

```json
[
    {"author": "Pete Hunt", "text": "This is one comment"},
    {"author": "Jordan Walke", "text": "This is *another* comment"}
]
```

We'll change our `app-state` var to be an empty map (within an atom,
that is) as well.

```clojure
(def app-state
  (atom {}))
```

When our application starts, we'll initialize our comments list to an
empty list, as done in the React tutorial (we're following along a bit
out of order now -- this is from tutorial14.js). We can do this using
the `om/IInitState` protocol that corresponds to the `getInitialState`
method in React. Update the `comment-box` definition as follows:

```clojure
(defn comment-box [app]
  (reify
    om/IInitState
    (init-state [_ owner]
      (om/update! app [:comments] (fn [] []))
    om/IRender
    ...))
```

[`om/update!`](https://github.com/swannodette/om/blob/master/src/om/core.cljs#L225)
takes a cursor, a list of keys, and a function -- and applies the
function to the data in the cursor at the path represented by the list
of keys. Here, we're just initializing our `:comments` to an empty
vector. `om/update!` alternatively takes just a cursor and function --
we could rewrite the above like so:

```clojure
...
(init-state [_ owner]
  (om/update! app #(assoc % :comments [])))
...
```

## Define Server-side logic

Before we can pull comments from the server, we need to define the
server-side logic to serve those comments. I'm not going to explain
how this works since it's not the point of this tutorial, but here's
the code that needs to go into `core.clj` (replace the existing stuff):

```clojure
(def comments (atom []))

(defn json-response [data & [status]]
  {:status (or status 200)
   :headers {"Content-Type" "application/json"}
   :body (json/generate-string data)})

(defn init
  []
  (reset! comments (-> (slurp "comments.json")
                       (json/parse-string true)
                       vec)))

(defn save-comment!
  [{:keys [body]}]
  (let [comment (-> body io/reader slurp (json/parse-string true))]
    (swap! comments conj comment)
    (json-response
     {:message "Saved comment!"})))

(defroutes app-routes
  (GET "/" [] (resp/redirect "/index.html"))

  (GET "/comments" [] (json-response
                       {:message "Here's the comments!"
                        :comments @comments}))
  (POST "/comments" req (save-comment! req))

  (route/resources "/")
  (route/not-found "Page not found"))

(def app
  (-> #'app-routes
      (handler/api)))
```

Finally, you'll need to add the following line to your `project.clj`
under the `:ring` key:

```clojure
:init    react-tutorial-om.core/init
```

---

## Fetching the comments

In the react tutorial they use the jQuery `$.ajax` function to hit the
server -- we're going to use
[cljs-http](https://github.com/r0man/cljs-http). It's a bit newer than
some of the other CLJS ajax libraries out there, but I like it due to
its use of `core.async`.

The endpoint that we want to hit is at `/comments` on the
server. Rather than hard-coding this endpoint into the functions that
use it directly, let's assume that it will show up multiple times in
the application and is subject to change at a later date. This is an
ideal candidate to pass as `:opts` to the component using it. Update
your `app` definition like so:

```clojure
(defn omtut-starter-app [app]
   ...
               (om/build comment-box app
                         {:opts {:url "/comments"}}))
```

Now, let's define a function to get our comments from the server.

```clojure
(defn- fetch-comments
  [url]
  (let [c (chan)]
    (go (let [{{comments :comments} :body} (<! (http/get url))]
          (>! c (vec (map with-id comments)))))
    c))
```

Given a url, the function above issues an http GET request, extracts
the comments from the body of the response, adds a `guid` to each
comment, puts the result of that request on a channel, and returns the
channel.

Now we just need to wire this function up to our component to make
sure that it gets run before the component is rendered. We'll do that
in the within the `IWillMount` method
([`componentWillMount`](http://facebook.github.io/react/docs/component-specs.html#mounting-componentwillmount)
in React), which is "invoked immediately before rendering occurs".

In your `comment-box` definition, add the following method:

```clojure
(defn comment-box [app opts]
  (reify
    om/IInitState
    ...
    om/IWillMount
    (will-mount [_ owner]
      (go (let [comments (<! (fetch-comments (:url opts)))]
            (om/update! app #(assoc % :comments comments)))))
    om/IRender
    ...))
```

We call `fetch-comments` with the url from `opts`, which will return a
channel. We use an asynchronous take (`<!`) to extract the comments,
and then update our cursor with a call to `om/update!`.

## Polling for updates

The last thing that we need to do is poll the server for updates --
we'd like to see other users' comments as they're posted. First, we'll
define an interval to poll at and pass this to the `comment-box` via
`opts`.

Add `:poll-interval 2000` to the `opts` map in the `app` definition to
poll every two seconds -- then, we'll modify the `IWillMount`
definition to loop every two seconds by using a `timeout` channel
(make sure that `timeout` is referred from `clojure.core.async` in
your `ns` definition).

```clojure
(defn comment-box [app opts]
  (reify
    ...
    om/IWillMount
    (will-mount [_ owner]
      (go (while true
            (let [comments (<! (fetch-comments (:url opts)))]
              (om/update! app #(assoc % :comments comments)))
            (<! (timeout (:poll-interval opts))))))
    ...))
```

And that's it!
