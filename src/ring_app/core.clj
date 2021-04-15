(ns ring-app.core
  (:require
    [reitit.ring as reitit]
    [muuntaja.middleware :as muuntaja]
    [ring.adapter.jetty :as jetty]
    [ring.util.http-response :as response]
    [ring.middleware.reload :refer [wrap-reload]]))

(defn html-handler [request-map]
  (response/ok
    (str "<html><body> your IP is: "
         (:remote-addr request-map)
         "</body></html>")))

(defn json-handler [request]
  (response/ok
    {:result (get-in request [:body-params :id])}))



;(def handler json-handler)

(defn wrap-nocache [handler]
  (fn [request]
    (-> request
        handler
        (assoc-in [:headers "Pragma"] "no-cache"))))

(defn wrap-formats [handler]
  (-> handler
      (muuntaja/wrap-format)))

(def routes
  [["/" {:get html-handler}]])

(def handler
  (reitit/ring-handler
    (reitit/router routes)))

;(defn test-repl
;  []
;  (print "repl connected"))

(defn -main []
  (jetty/run-jetty
    (-> #'handler
        wrap-nocache
        wrap-formats
        wrap-reload)
    {:port 3000
     :join? false}))