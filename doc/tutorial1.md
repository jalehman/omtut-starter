[Your first component](http://facebook.github.io/react/docs/tutorial.html#your-first-component)
====================

## Getting familiar

The bulk of the ClojureScript (CLJS) code that you'll be writing will
live in `src/cljs/core.cljs`. The server-side code is in
`src/clj/`. We won't be covering any server-side stuff in these
tutorials, but the code will update in this repo when necessary, so
feel free to check it out.

**Note:** This is not a Clojure/Script tutorial -- I'll assume that
you are already moderately familiar with CLJ/S. If not, I would
recommend becoming familiar with them beforehand.

At this point core.cljs should look something like this:

```clojure
(enable-console-print!)

(def app-state
  (atom {:things []}))

(defn omtut-starter-app [app]
  (reify
    om/IRender
    (render [_ owner]
      (dom/div nil
               (dom/h1 nil "omtut-starter is working!")))))

(om/root app-state omtut-starter-app (.getElementById js/document "content"))
```

The first line allows us to write code like `(prn <cljs-data-here>)`
and see something useful in the console -- much better than `(.log
js/console <stuff>)` both in terms of code legibility and quality of
output. If you don't believe me, try the following and compare the results:

```clojure
(prn {:a 1 :b ["s" {:c 2}]})
(.log js/console {:a 1 :b ["s" {:c 2}]})
```

The next block is a an atom that will hold your application's mutable
state, which will be passed throughout the application.

The third block of code is the top-level component definition, or
"app" definition.

The final block of code defines the entry point of sorts for the
application.

## What's going on?

Let's start with the entry point for the application -- `om/root`.

`om/root` is a function that takes "an immutable value or value
wrapped in an atom [if it's not an atom, Om will make it one], an
initial function f, and a DOM target."

```clojure
(om/root
  app-state
  omtut-starter-app
  (.getElementById js/document "content"))
```

is kind of like

```javascript
React.renderComponent(
  OmtutStarterApp(appState),
  document.getElementById('content')
);
```

in React.

The data passed as a first argument will be annotated with information
that tells Om internally which bits are state and how to look them up
("state and path respectively").

That data is fed to the "function f", which must be a component that
implements `IRender` (more on that momentarily).

Finally, the last argument tells Om where to install the React render
loop (watch for updates, perform updates, repeat).

As mentioned in the React tutorial
[here](http://facebook.github.io/react/docs/tutorial.html#whats-going-on),
React defines a number of methods available to components which are
implemented as protocols in Om.

```clojure
(defn omtut-starter-app [app]
  (reify
    om/IRender
    (render [_ owner]
    ...)))
```

is something like

```javascript
var OmtutStarterApp = React.createClass({
  render: function() {
   return (
     ...
   );
  }
});
```

in React.

We frequently only need to implement the `render` method of a
component, so Om has a convenience macro for that. The definition
of `omtut-starter-app` could be rewritten as:

```clojure
(defn omtut-starter-app [app]
  (om/component
    (dom/div nil
        (dom/h1 nil "omtut-starter is working!"))))
```

Feel free to use either form for now, although we'll eventually need
the expanded definition.

## Tutorial 1

We need to define a `CommentBox` component. Let's use the shortened
form of an Om component shown above to do so now.

```clojure
(defn comment-box [app]
  (om/component
   (dom/div #js {:className "commentBox"}
            "Hello, world! I am a CommentBox."))))
```

Just as React defines JSX for working with HTML-looking elements in JS
-- Om defines a set of macros in the `dom` namespace that allow us to
build React dom components (**aside:** the project
[sablono](https://github.com/r0man/sablono) by
[r0man](https://github.com/r0man) provides a way of expressing these
elements in [hiccup](https://github.com/weavejester/hiccup)-style
syntax -- super cool). We define components in s-expressions, passing
attributes as a map of `{:attribute "value"}` and defining children as
the last argument(s). The `#js` is just shorthand for expressing js
objects/arrays (thanks [swannodette](https://github.com/swannodette)
for the clarification on that).

Finally, modify the app definition to render this new component:

```clojure
(defn omtut-starter-app [app]
  (reify
    om/IRender
    (render [_ owner]
      (dom/div nil
               (om/build comment-box app)))))
```

We replace the `h1` component with our custom one by "building" it: a
call to `om/build` with the component and application state (cursor)
as arguments does the trick. Refresh your page (assuming `lein dev` is
running) to see it in action!
