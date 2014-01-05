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

(defn comment [app owner {:keys [author text] :as opts}]
  (om/component
   (dom/div #js {:className "comment"}
            (dom/h2 #js {:className "commentAuthor"} author)
            text)))

(defn comment-list [app]
  (om/component
   (dom/div #js {:className "commentList"}
            (om/build comment app {:opts {:author "Pete Hunt"
                                          :text "This is one comment"}})
            (om/build comment app {:opts {:author "Jordan Walke"
                                          :text "This is *another* comment"}}))))

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

(om/root app-state omtut-starter-app (.getElementById js/document "content"))
