(ns omtut-starter.core
    (:require [compojure.handler :as handler]
              [compojure.route :as route]
              [compojure.core :refer [GET POST defroutes]]
              [ring.util.response :as resp]
              [cheshire.core :as json]
              [clojure.java.io :as io]))

(defn json-response [data & [status]]
  {:status (or status 200)
   :headers {"Content-Type" "application/json"}
   :body (json/generate-string data)})

(defroutes app-routes
  (GET "/" [] (resp/redirect "/index.html"))

  (GET "/test" [] (json-response
                   {:message "You made it!"}))

  (POST "/test" req (json-response
                     {:message "Doing something something important..."}))

  (route/resources "/")
  (route/not-found "Page not found"))

(def app
  (-> #'app-routes
      (handler/api)))
