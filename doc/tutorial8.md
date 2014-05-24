# [Hook up the data model](http://facebook.github.io/react/docs/tutorial.html#hook-up-the-data-model) (tutorials 8-10)

Up to this point we've enumerated individual `comment` components and
built them with explicit properties. Ideally, these comments would be
pulled from some sort of server-side backend into a list of comments and rendered
in sequence.

Before getting into pulling them from a backend, we'll simulate the
effect by hard-coding the comments we want.

The cursor is a good place to represent this – Om watches the cursor
for changes and updates the UI accordingly.

```clojure
(def app-state
  (atom {:comments [{:author "Pete Hunt" :text "This is one comment"}
                    {:author "Jordan Walke" :text "This is *another* comment"}]}))
```

Now, since each component is passed a copy of the cursor, we just need
to tell Om where to find our data and what to do with it. Change your
`comment-list` as follows:

```clojure
(defn comment-list [{:keys [comments]}]
  (om/component
   (dom/div #js {:className "commentList"}
            (into-array (om/build-all comment comments))))
```

<!-- Here we build a list of two comments, specifying the `:path` to the -->
<!-- data that should be passed to the `comment` component. This is another -->
<!-- of the keys that that `om/build`'s third argument can take. Don't -->
<!-- forget the `into-array` call to convert the CLJS list into a JS array. -->

Here we destructure the cursor to expose the comments, and use the Om
`build-all` function to construct a sequence of
components. `build-all` takes three arguments (the third is optional):
a component constructor, the sequence of cursors, and options just like the
ones we passed to `build`.

<!-- The important piece of the third argument is -->
<!-- the unique `:id` key that allows Om to distinguish repeated comments. -->

Finally, we modify the `comment` component to destructure the cursor
rather than the options:

```clojure
(defn comment [{:keys [author text] :as c} owner opts]
  ...)
```

This code will work fine, but if you open up the console you'll notice
a warning: `Each child in an array should have a unique "key"
prop. Check the render method of undefined.` The problem is that React
has no way of uniquely identifying which element is which in the
sequence of rendered components. To fix this, we'll specify a unique
key in each element for React to differentiate between each element in
the sequence.

Modify `app-state` as follows:

```clojure
(def app-state
  (atom {:comments [{:author "Pete Hunt" :text "This is one comment" :id (guid)}
                    {:author "Jordan Walke" :text "This is *another* comment" :id (guid)}]}))
```

The `guid` function in the `utils.cljs` namespace uses the Google
Closure
[ui.IdGenerator](http://docs.closure-library.googlecode.com/git/class_goog_ui_IdGenerator.html)
method to generate a unique id for each element.

Next, we modify the `comment-list` component:

```clojure
(defn comment-list [{:keys [comments]}]
  (om/component
   (dom/div #js {:className "commentList"}
            (om/build-all comment comments
                          {:key :id}))))
```

We specify a way in the options map to distinguish these comments from
each other via the `:key` property.

Now we're done with tutorial 10.
