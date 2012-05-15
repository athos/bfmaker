(ns bfmaker.test.interp.tokenizer
  (:use [clojure.test :only [deftest is are]])
  (:require [bfmaker.interp.tokenizer :as tk]))

(deftest test-add-to-trie
  (is (= (-> (tk/make-trie)
             (tk/add-to-trie "ab")
             (tk/add-to-trie "abcde")
             (tk/add-to-trie "bab")
             (tk/add-to-trie "bc")
             (tk/add-to-trie "d")
             (tk/trie->map))
         [nil {\a [nil
                   {\b ["ab"
                        {\c [nil
                             {\d [nil
                                  {\e ["abcde" {}]}]}]}]}]
               \b [nil
                   {\a [nil
                        {\b ["bab" {}]}]
                    \c ["bc" {}]}]
               \d ["d" {}]}])))

(deftest test-trie->regexp
  (let [re (-> (tk/make-trie)
               (tk/add-to-trie "program")
               (tk/add-to-trie "programist")
               (tk/add-to-trie "programmatic")
               (tk/add-to-trie "programmer")
               (tk/trie->regexp))]
    (is (re-matches re "program"))
    (is (re-matches re "programist"))
    (is (not (re-matches re "programistic")))
    (is (not (re-matches re "programme")))
    (is (re-matches re "programmer"))))

(deftest test-tokenizer
  (let [tokenizer (tk/make-tokenizer {"ab" :ab
                                      "abcde" :abcde
                                      "bab" :bab
                                      "bc" :bc
                                      "d" :d})]
    (is (= (tokenizer "xbabcdex")
           [:bab :d]))))
