(ns bfmaker.test.handler
  (:use [clojure.data.json :only [read-json-from]]
   [clojure.test :only [deftest is]])
  (:require [bfmaker.handler :as handler]))

(deftest test-eval-handler
  (let [program (str "+++++++++[>++++++++>+++++++++++>+++++<<"
                     "<-]>.>++.+++++++..+++.>-.------------.<"
                     "++++++++.--------.+++.------.--------.>+.")
        {:keys [_ body]} (handler/eval-handler nil program "")
        {:keys [status result]} (read-json-from body true false nil)]
    (is (= status 200))
    (is (= result "Hello, world!")))

  (let [program "+[]"
        {:keys [_ body]} (handler/eval-handler nil program "")
        {:keys [status result]} (read-json-from body true false nil)]
    (is (= status 400))
    (is (= result "timeout"))))
