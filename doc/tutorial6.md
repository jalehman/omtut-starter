# [Adding Markdown](http://facebook.github.io/react/docs/tutorial.html#adding-markdown) (tutorials 6-7)

Rather than using Showdown as used in the React tutorial, we'll use a
CLJS library -- that'll keep things more idiomatic.

The library we'll use is
[markdown-clj](https://github.com/yogthos/markdown-clj). Add
`[markdown-clj "0.9.38"]` to your `project.clj`, and
`[markdown.core :as md]` to the `:require` block of the `core.cljs`
namespace.

```clojure
(defn comment [app owner {:keys [author text] :as opts}]
  (om/component
   (let [raw-markup (md/mdToHtml text)]
     (dom/div #js {:className "comment"}
              (dom/h2 #js {:className "commentAuthor"} author)
              (dom/span #js {:dangerouslySetInnerHTML #js {:__html raw-markup}})))))
```

The relevant pieces here are almost a straight translation from the
original React code. We use `(dom/span #js {:dangerouslySetInnerHTML #js {:__html raw-markup}})` to render the comments as markdown.

Since dependencies have changed, make sure to restart `lein dev`.
