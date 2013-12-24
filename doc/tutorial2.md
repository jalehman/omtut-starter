# [Composing Components](http://facebook.github.io/react/docs/tutorial.html#composing-components) (tutorial 2-3)

Let's create scaffold two more components using the `om/component`
convenience macro:

```clojure
(defn comment-list [app]
  (om/component
   (dom/div #js {:classname "commentList"}
            "Hello, world! I am a CommentList.")))

(defn comment-form [app]
  (om/component
   (dom/div #js {:classname "commentForm"}
            "Hello, world! I am a CommentForm.")))
```

and add them to our `comment-box` component.

```clojure
(defn comment-box [app]
  (om/component
   (dom/div #js {:className "commentBox"}
            (dom/h1 nil "Comments")
            (om/build comment-list app)
            (om/build comment-form app))))
```

Nothing new here.
