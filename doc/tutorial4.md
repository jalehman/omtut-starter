# [Component Properties](http://facebook.github.io/react/docs/tutorial.html#component-properties) (tutorials 4-5)

In Om, we have two facilities for passing data into our components:

1. Passing data in options directly to a component,
2. accessing application state.

In this case, the first option is probably the closest to the React
version.

Let's start by rendering two `comment` components (we'll define the
component itself afterwards) and define the `author` and `text`
properties as options.

```clojure
(defn comment-list [app]
  (om/component
   (dom/div #js {:className "commentList"}
            (om/build comment app {:opts {:author "Pete Hunt"
                                          :text "This is one comment"}})
            (om/build comment app {:opts {:author "Jordan Walke"
                                          :text "This is *another* comment"}}))))
```

## [Using Props](http://facebook.github.io/react/docs/tutorial.html#using-props)

Let's define our `comment` component. Now, instead of just taking
`app` (the cursor) as an argument, we'll also take `opts` (and
destructure them).

```clojure
(defn comment [app {:keys [author text] :as opts}]
  (om/component
   (dom/div #js {:className "comment"}
            (dom/h2 #js {:className "commentAuthor"} author)
            text)))
```

`opts` is one key of several that can be passed as the last argument
to `om/build`. We'll explore more of these later.
