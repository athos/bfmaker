(ns bfmaker.test.interp.vm
  (:use [clojure.test :only [deftest is]])
  (:require [bfmaker.interp.vm :as vm]))

(deftest test-parse
  (is (= (vm/parse [:inc :while :forth :inc :while :dec :write :end-while :back :end-while :read :dec])
         [[:inc [:forth :inc [:dec :write] :back] :read :dec] nil])))

(deftest test-execute
  (let [code [:inc :inc :inc :inc :inc :inc :inc :inc :inc
              [:forth :inc :inc :inc :inc :inc :inc
               :inc :inc :forth :inc :inc :inc :inc
               :inc :inc :inc :inc :inc :inc :inc :forth
               :inc :inc :inc :inc :inc :back :back :back :dec]
              :forth :write :forth :inc :inc :write :inc :inc
              :inc :inc :inc :inc :inc :write :write :inc :inc
              :inc :write :forth :dec :write :dec :dec :dec
              :dec :dec :dec :dec :dec :dec :dec :dec :dec
              :write :back :inc :inc :inc :inc :inc :inc :inc
              :inc :write :dec :dec :dec :dec :dec :dec :dec
              :dec :write :inc :inc :inc :write :dec :dec
              :dec :dec :dec :dec :write :dec :dec :dec :dec
              :dec :dec :dec :dec :write :forth :inc :write
              :inc :inc :inc [:forth :inc :inc :back :dec]]]
    (is (= (with-out-str
             (vm/execute code (vec (repeat 5 0)) 0))
           "Hello, world!")))

  (is (thrown-with-msg? IllegalStateException
        #"^overflow at"
        (vm/execute [:inc] [vm/+max-value+] 0)))
  (is (thrown-with-msg? IllegalStateException
        #"^underflow at"
        (vm/execute [:dec] [0] 0)))
  (is (thrown-with-msg? IllegalStateException
        #"^pointer overflow$"
        (vm/execute [:forth] [0] 0)))
  (is (thrown-with-msg? IllegalStateException
        #"^pointer underflow$"
        (vm/execute [:back] [0] 0))))
