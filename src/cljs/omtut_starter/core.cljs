(ns omtut-starter.core
  (:require-macros [cljs.core.async.macros :refer [go alt!]])
  (:require [goog.events :as events]
            [cljs.core.async :refer [put! <! >! chan timeout]]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [cljs-http.client :as http]
            [omtut-starter.utils :refer [guid]]))

(enable-console-print!)

(def app-state
  (atom {:things []}))

(defn comment-list [app]
  (om/component
   (dom/div #js {:className "commentList"}
     "Hello, world! I am a CommentList.")))

(defn comment-form [app]
  (om/component
   (dom/div #js {:className "commentForm"}
     "Hello, world! I am a CommentForm.")))

(defn comment-box [app]
  (om/component
   (dom/div #js {:className "commentBox"}
     (dom/h1 nil "Comments")
     (om/build comment-list app)
     (om/build comment-form app))))

(defn omtut-starter-app [app owner]
  (reify
    om/IRender
    (render [_]
      (dom/div nil
        (om/build comment-box app)))))

(om/root omtut-starter-app app-state {:target (.getElementById js/document "content")})
