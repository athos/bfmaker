(ns bfmaker.core
  (:use [compojure.core :only [defroutes GET POST ANY]]
        [compojure.route :only [resources not-found]]
        [compojure.handler :only [site]]
        [ring.adapter.jetty :only [run-jetty]]))

(defn dummy-handler [path]
  {:status 200
   :headers {"Content-Type" "text/plain"}
   :body (str "This page is at " path)})

(defroutes routes
  (GET "/" req
       (dummy-handler "/"))
  (GET "/new" req
       (dummy-handler "/new"))
  (POST "/new" req
        (dummy-handler "/new"))
  (POST "/save" req
        (dummy-handler "/save"))
  (GET "/api/eval" req
       (dummy-handler "/api/eval"))
  (POST "/api/eval" req
       (dummy-handler "/api/eval"))
  (GET "/api/translate" req
       (dummy-handler "/api/translate"))
  (POST "/api/translate" req
        (dummy-handler "/api/translate"))
  (GET "/api/recent" req
       (dummy-handler "/api/recent"))
  (GET "/:lang" req
       (dummy-handler "/<lang>"))
  (POST "/:lang" req
        (dummy-handler "/<lang>"))
  (GET "/:lang/:code" req
       (dummy-handler "/<lang>/<code>"))
  (resources "/")
  (not-found "not found"))

(def twidare-app (site routes))

(defn -main []
  (let [port (Integer/parseInt (get (System/getenv) "PORT" "8080"))]
    (run-jetty twidare-app {:port port})))
