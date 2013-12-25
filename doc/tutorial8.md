# [Hook up the data model](http://facebook.github.io/react/docs/tutorial.html#hook-up-the-data-model) (tutorials 8-10)

Up to this point we've enumerated individual `comment` components and
built them with explicit properties. Ideally, these comments would be
pulled from some sort of back-end into a list of comments and rendered
in sequence.

Before getting into pulling them from a back-end, we'll simulate the
effect by hard-coding the comments we want.

The cursor is a good place to represent this -- Om watches the cursor
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
(defn comment-list [app]
  (om/component
   (dom/div #js {:className "commentList"}
            (into-array
             (map #(om/build comment app
                             {:path [:comments]})
                  (range (count (:comments app))))))))
```

Here we build a list of two comments, specifying the `:path` to the
data that should be passed to the `comment` component. This is another
of the keys that that `om/build`'s third argument can take. Don't
forget the `into-array` call to convert the CLJS list into a JS array.

Finally, we modify the `comment` component to destructure the cursor
rather than the options:

```clojure
(defn comment [{:keys [author text] :as c} opts]
  ...)
```

This code will compile fine, but you should get an `Undefined
nameToPath` error. Doh!

The problem is that we've specified a path to a *vector* of maps, and
the `comment` component is expecting a single map. We need to define a
way to identify which object to be rendered. To accomplish this, we'll
do two things:

1. Extend the path to reference the index of the element to render, and
2. Specify a unique key in each element for React to differentiate
   between each element in the sequence.

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
(defn comment-list [app]
  (om/component
   (dom/div #js {:className "commentList"}
            (into-array
             (map #(om/build comment app
                             {:path [:comments %]
                              :key :id})
                  (range (count (:comments app))))))))
```

We extend the `:path` to now point to the index of the specific
element within the `:comments` list. We also specify the unique `:key`
(`:id`) that contains the guid of the element so that React can
differentiate it from other elements when rendering updates. (Is this
correct?)

Now we're complete through tutorial 10.
