(ns bfmaker.handler)

;;
;; Page handler
;;
(defmacro def-page-handler [name args & body]
  `(defn ~name ~args
     (let [ret# (do ~@body)]
       {:status 200
        :headers {"Content-Type" "text/plain"}
        :body ret#})))

(def-page-handler root-handler []
  "This page is at /.")

(def-page-handler new-handler []
  "This page is at /new.")

(def-page-handler new-post-handler [req]
  (let [{:keys [name description hashtag inc dec forth back while end-while read write fork]} req]
   "This page is at /new."))

(def-page-handler save-handler [lang-id code]
  "This page is at /save.")

(def-page-handler lang-page-handler [lang-id]
  (str "This page is at /" lang-id "."))

(def-page-handler code-page-handler [lang-id code-id]
  (str "This page is at /" lang-id "/" code-id "."))

;;
;; API handler
;;
(defmacro def-api-handler [name args & body]
  `(defn ~name ~args
     (let [ret# (do ~@body)]
       {:status 200
        :headers {"Content-Type" "application/json"}
        :body ret#})))

(def-api-handler eval-handler [lang-id code input]
  "This page is at /api/eval.")

(def-api-handler translate-handler [lang-id code]
  "This page is at /api/translate.")

(def-api-handler recent-handler [since-id count]
  "This page is at /api/recent.")
