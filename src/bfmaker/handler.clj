(ns bfmaker.handler
  (:use [clojure.data.json :only [json-str]]
        [bfmaker.interp :only [make-interpreter]]
        [bfmaker.model :only [fetch-lang]]))

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
     (let [result# (do ~@body)]
       {:status 200
        :headers {"Content-Type" "application/json"}
        :body (json-str result#)})))

(defn succeed [result] {:status 200, :result result})
(defn fail [status message] {:status status, :result message})

(def-api-handler eval-handler [lang-id program input]
  (if-let [word-map (fetch-lang lang-id)]
    (let [memory (vec (repeat 256 0))
          interp (make-interpreter word-map memory 0)
          f (future
              (try
                (succeed (with-out-str
                           (with-in-str (or input "")
                             (interp program))))
                (catch IllegalStateException e
                  (fail 400 (.getMessage e)))))
          result (deref f 5000 nil)]
      (or result
          (fail 400 "timeout")))
    (fail 404 (str "lang-id " lang-id " not found"))))

(def-api-handler translate-handler [lang-id code]
  "This page is at /api/translate.")

(def-api-handler recent-handler [since-id count]
  "This page is at /api/recent.")
