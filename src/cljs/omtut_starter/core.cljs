(ns omtut-starter.core
  (:require-macros [cljs.core.async.macros :refer [go alt!]])
  (:require [goog.events :as events]
            [cljs.core.async :refer [put! <! >! chan timeout]]
            [markdown.core :as md]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [cljs-http.client :as http]
            [omtut-starter.utils :refer [guid]]))

(enable-console-print!)

(defn- with-id
  [m]
  (assoc m :id (guid)))

(defn- fetch-comments
  [url]
  (let [c (chan)]
    (go (let [{{comments :comments} :body} (<! (http/get url))]
          (>! c (vec (map with-id comments)))))
    c))

(def app-state
  (atom {}))

(defn comment [{:keys [author text] :as c} opts]
  (om/component
   (let [raw-markup (md/mdToHtml text)]
     (dom/div #js {:className "comment"}
              (dom/h2 #js {:className "commentAuthor"} author)
              (dom/span #js {:dangerouslySetInnerHTML #js {:__html raw-markup}})))))

(defn comment-list [app]
  (om/component
   (dom/div #js {:className "commentList"}
            (into-array
             (map #(om/build comment app
                             {:path [:comments %]
                              :key :id})
                  (range (count (:comments app))))))))

(defn comment-form [app]
  (om/component
   (dom/div #js {:className "commentForm"}
            "Hello, world! I am a CommentForm.")))

(defn comment-box [app opts]
  (reify
    om/IInitState
    (init-state [_ owner]
      (om/update! app [:comments] (fn [] [])))
    om/IWillMount
    (will-mount [_ owner]
      (go (while true
            (let [comments (<! (fetch-comments (:url opts)))]
              (om/update! app #(assoc % :comments comments)))
            (<! (timeout (:poll-interval opts))))))
    om/IRender
    (render [_ owner]
      (dom/div #js {:className "commentBox"}
               (dom/h1 nil "Comments")
               (om/build comment-list app)
               (om/build comment-form app)))))

(defn omtut-starter-app [app]
  (reify
    om/IRender
    (render [_ owner]
      (dom/div nil
               (om/build comment-box app
                         {:opts {:url "/comments"
                                 :poll-interval 2000}})))))

(om/root app-state omtut-starter-app (.getElementById js/document "content"))
