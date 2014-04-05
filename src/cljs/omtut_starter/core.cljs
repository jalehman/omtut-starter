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
          (>! c (mapv with-id comments))))
    c))

(def app-state
  (atom {}))

(defn comment [{:keys [author text]} owner opts]
  (om/component
   (let [raw-markup (md/mdToHtml text)]
     (dom/div #js {:className "comment"}
       (dom/h2 #js {:className "commentAuthor"} author)
       (dom/span #js {:dangerouslySetInnerHTML #js {:__html raw-markup}})))))

(defn comment-list [{:keys [comments]}]
  (om/component
   (apply dom/div #js {:className "commentList"}
          (om/build-all comment comments
            {:key :id}))))

(defn comment-form [app]
  (om/component
   (dom/div #js {:className "commentForm"}
     "Hello, world! I am a CommentForm.")))

(defn comment-box [app owner opts]
  (reify
    om/IInitState
    (init-state [_]
      (om/transact! app [:comments] (fn [] [])))
    om/IWillMount
    (will-mount [_]
      (go (while true
            (let [comments (<! (fetch-comments (:url opts)))]
              (om/update! app #(assoc % :comments comments)))
            (<! (timeout (:poll-interval opts))))))
    om/IRender
    (render [_]
      (dom/div #js {:className "commentBox"}
        (dom/h1 nil "Comments")
        (om/build comment-list app)
        (om/build comment-form app)))))

(defn omtut-starter-app [app owner]
  (reify
    om/IRender
    (render [_]
      (dom/div nil
        (om/build comment-box app
          {:opts {:url "/comments"
                  :poll-interval 2000}})))))


(om/root omtut-starter-app app-state {:target (.getElementById js/document "content")})
