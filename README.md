Om Tutorial: Starter
====================

## Motivation

See my [blog post](http://www.joshlehman.me/rewriting-the-react-tutorial-in-om/).

## Intro

This is a walkthrough of sorts for the
[React tutorial](http://facebook.github.io/react/docs/tutorial.html)
rewritten in [Om](https://github.com/swannodette/om). I originally
made [this repo](https://github.com/jalehman/react-tutorial-om) a
little while back, but wanted to offer something similar to the
original tutorial that explains exactly what is going on at each step.

The walkthrough is organized by using a separate separate branch to
represent the different steps of the original tutorial, and the master
branch represents the complete code. The documentation for each step
is contained within the `/doc` folder.

## Getting Started

For getting the latest version of Om, follow swannodette's instructions [here](https://github.com/swannodette/todomvc/tree/gh-pages/labs/architecture-examples/om).

Create a new project template with `lein new om-starter
<name-of-repo>` substituting `<name-of-repo>` with whatever you want
to call the repository. The `/doc` directory contains files
`tutorial1.md` through `tutorial15.md` that correspond 1:1 in
functionality (JS and CLJS don't always have a 1:1 mapping), with the
original tutorial. Many of the files contain several of the original
tutorials.

After creating your repo with the lein template `om-starter`, `cd`
into it and run `lein dev`. This will start up a minimal web server
that will serve up your HTML and JS, and compile the CLJS into JS. The
server will likely start before the JS is compiled on the first go, so
after the message `Successfully compiled
"resources/public/js/your_repo.js" in #ofseconds seconds.` shows up in
the terminal window, refresh your page to verify that everything is
working correctly.

Time to hack.
