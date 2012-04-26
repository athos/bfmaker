(ns bfmaker.core
  (:use [compojure.core :only [defroutes GET POST ANY]]
        [compojure.route :only [resources not-found]]
        [compojure.handler :only [site]]
        [ring.adapter.jetty :only [run-jetty]])
  (:require [bfmaker.handler :as handler]))

(defroutes routes
  (GET "/" []
       (handler/root-handler))
  (GET "/new" []
       (handler/new-handler))
  (POST "/new" req
        (handler/new-post-handler req))
  (POST "/save" [lang-id code]
        (handler/save-handler lang-id code))
  (ANY "/api/eval" [lang-id code input]
       (handler/eval-handler lang-id code input))
  (ANY "/api/translate" [lang-id code]
       (handler/translate-handler lang-id code))
  (GET "/api/recent" [since-id count]
       (handler/recent-handler since-id count))
  (ANY "/:lang-id" [lang-id]
       (handler/lang-page-handler lang-id))
  (GET "/:lang-id/:code-id" [lang-id code-id]
       (handler/code-page-handler lang-id code-id))
  (resources "/")
  (not-found "not found"))

(def twidare-app (site routes))

(defn -main []
  (let [port (Integer/parseInt (get (System/getenv) "PORT" "8080"))]
    (run-jetty twidare-app {:port port})))
