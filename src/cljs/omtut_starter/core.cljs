(ns omtut-starter.core
    (:require-macros [cljs.core.async.macros :refer [go alt!]])
    (:require [goog.events :as events]
              [cljs.core.async :refer [put! <! >! chan timeout]]
              [om.core :as om :include-macros true]
              [om.dom :as dom :include-macros true]
              [cljs-http.client :as http]
              [omtut-starter.utils :refer [guid]]))

;; Lets you do (prn "stuff") to the console
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
