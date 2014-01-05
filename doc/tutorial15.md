# [Adding new comments](http://facebook.github.io/react/docs/tutorial.html#adding-new-comments) (tutorial 15-19)

Now that we can read comments from the server, it would be great if we
could write them to it as well. Let's start by building out the
`comment-form` to look more like an actual form.

```clojure
(defn comment-form [app]
  (om/component
   (dom/form
    #js {:className "commentForm"}
    (dom/input #js {:type "text" :placeholder "Your Name"})
    (dom/input #js {:type "text" :placeholder "Say something..."})
    (dom/input #js {:type "submit" :value "Post"}))))
```

Now that we have a more form-y looking form, we need to add a handler
for the `onSubmit` event. When triggered, our handler should:

1. Clear the form,
2. Submit a request to the server, and
3. Refresh the list of comments.

You'll notice now that if you click the "Post" button, the page will
refresh -- not good. Let's wire up a handler that will not only clear
the form on submit, but fix that issue in the process.

In order to access the values in the inputs, we'll need to assign
`ref` attributes to them and have access to the underlying
component. This means that we'll need to use the expanded component
definition.

```clojure
(defn comment-form [app owner opts]
  (reify
    om/IRender
    (render [_]
      (dom/form
       #js {:className "commentForm" :onSubmit #(handle-submit % owner opts)}
       (dom/input #js {:type "text" :placeholder "Your Name" :ref "author"})
       (dom/input #js {:type "text" :placeholder "Say something..." :ref "text"})
       (dom/input #js {:type "submit" :value "Post"})))))
```

I've defined the `onSubmit` handler as an anonymous function that
takes the event as its first argument, the component itself (exposed
as `owner`), and the `opts` map. We pass the `opts` map because we
need the url of the endpoint we're posting to. There are also `ref`
attributes on the two inputs now to provide access to the value in the
input node. Remember to update the `comment-box` definition to pass
its opts to `comment-form`:

```clojure
(defn comment-box [app opts]
   ...
               (om/build comment-form app {:opts opts})))))
```

Our `onSubmit` handler is going to need to extract values from input nodes, clear
the values from nodes, and POST a comment to the `/comments`
endpoint. Let's define some helper functions to handle these tasks.

First, getting the value of a node:

```clojure
(defn- value-from-node
  [component field]
  (let [n (om/get-node component field)
        v (-> n .-value clojure.string/trim)]
    (when-not (empty? v)
      [v n])))
```

The function `om/get-node` takes a component and the name of a ref and
returns that ref. We then use the ref to extract the value and trim
leading/trailing whitespace, just like in the original tutorial. We
return the value *and* node, or `nil`. The reason we return the node
is because we'll need to clear it afterwards -- let's define that
function now. It will take a variable number of nodes and set the
value contained within them to an empty string.

```clojure
(defn- clear-nodes!
  [& nodes]
  (doall (map #(set! (.-value %) "") nodes)))
```

Next we'll define a function to save a comment to the server (we
defined the logic to save the comments in memory in the last
tutorial).

```clojure
(defn save-comment!
  [comment url]
  (go (let [res (<! (http/post url {:json-params comment}))]
        (prn (get-in res [:body :message])))))
```

Finally, we have the tools we need to define the handler itself:

```clojure
(defn handle-submit
  [e owner opts]
  (let [[author author-node] (value-from-node owner "author")
        [text text-node]     (value-from-node owner "text")]
    (when (and author text)
      (save-comment! {:author author :text text} (:url opts))
      (clear-nodes! author-node text-node))
    false))
```

First we use `value-from-node` to extract the values in the input
nodes. When there's a value in *both* of these fields, we call
`save-comment!` to persist the comment, and then call `clear-nodes!`
on the nodes returned from `value-from-node`.

Our component is now fairly feature-complete. We can read a list of
comments from a server, save new comments, have that list update when
comments from other clients are saved, and there's even some
rudimentary validation.

## [Optimization: optimistic updates](http://facebook.github.io/react/docs/tutorial.html#optimization-optimistic-updates) (tutorial 20)

A simple way to make the component better would be to update the
comment list upon entry of a new comment *immediately*, rather than
waiting for our polling to pick it up.

Instead of just sending the comment off to the server, we can also
update our cursor with a call to `om/transact!` (which triggers a
component render).

```clojure
(defn handle-submit
  [e owner app opts]
  (let [[author author-node] (value-from-node owner "author")
        [text text-node]     (value-from-node owner "text")]
    (when (and author text)
      (let [comment {:author author :text text}]
        (save-comment! comment (:url opts))
        (om/transact! app [:comments]
                    (fn [comments] (conj comments (assoc comment :id (guid))))))
      (clear-nodes! author-node text-node))
    false))
```

We modify `handle-submit` to take the cursor as an argument, and
include a call to `om/transact!` to add our comment to the existing
comments. That concludes the full React tutorial!

## What now?

There are quite a few things that could be improved about this
component that I've been thinking about, and I will probably try to
implement some of my ideas at one point. Until I get around to doing
these myself in tutorial form, here are some ideas that can be used as
exercises/practice problems for the reader:

1. So far we've got *Create* and *Read* of CRUD. How about the *D* and
   *U*?
   + Only the author (let's not bother with admins) should be able to
     update and delete his or her own comments. How about some kind of
     basic system for entering your name at the start of the
     application, and then restricting update/delete to the comments
     where the name matches?
2. Comments are generally not a standalone piece of an application --
   they accompany some other piece of content. How would you attach
   a CommentBox to another component containing subject matter?
   + It's often nice to be able to show and hide the list of
     comments. How would you make the CommentBox showable/hideable?
     What would be a good way to make *anything* showable/hideable in
     Om?
3. Not all comments are equal. Some are more interesting than others
   -- which is why sites like Hacker News & Reddit have up/downvoting
   capabilities. What would be a good way to add up/downvoting to our
   Comments?
   + A person should only be able to upvote or downvote something; not
     both simultaneously.
   + The aggregate amount of up/downvotes should be displayed
     somewhere (duh).
   + Make sure that one user can't do more than one up/downvote.
   + Should total score of a comment be factor into the sorting order
     of the comments? What about arbitrarily changing the sorting
     order of the comments?
4. While we're on the subject of HN/Reddit, how about **replies** to
   comments?
5. Hell, why not just make a Hacker News clone!
