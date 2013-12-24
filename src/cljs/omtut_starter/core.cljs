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

(defn comment-box [app]
  (om/component
   (dom/div #js {:className "commentBox"}
            (dom/h1 nil "Hello, world! I am a CommentBox."))))

(defn omtut-starter-app [app]
  (reify
    om/IRender
    (render [_ owner]
      (dom/div nil
               (om/build comment-box app)))))

(om/root app-state omtut-starter-app (.getElementById js/document "content"))
