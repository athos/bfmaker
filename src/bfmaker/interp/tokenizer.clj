(ns bfmaker.interp.tokenizer
  (:use [clojure.string :only [join]])
  (:import java.util.regex.Pattern))

(deftype Node [word children]
  clojure.lang.IFn
  (invoke [this x]
    ((.children this) x)))

(defn make-trie []
  (->Node nil {}))

;; for debug
(defn trie->map [^Node trie]
  [(.word trie)
   (into {} (map (fn [[k v]] [k (trie->map v)]) (.children trie)))])

(defn add-to-trie
  ([trie word] (add-to-trie trie word word))
  ([^Node trie ccs word]
     (if (empty? ccs)
       (->Node word (.children trie))
       (let [[c & cs] ccs
             trie' (or (trie c) (make-trie))
             children (.children trie)]
         (->Node (.word trie)
                 (assoc children c (add-to-trie trie' cs word)))))))

(defn- %trie->regexp [^Node trie]
  (let [children (.children trie)]
    (loop [[[c ^Node trie'] & children' :as children] (seq children), alt [], cc []]
      (if (empty? children)
        (let [opt (fn opt [s] (cond (not (.word trie)) s
                                    (empty? alt) (str s \?)
                                    :else (str "(?:" s ")?")))
              alt (case (count cc)
                    0 alt
                    1 (conj alt (first cc))
                    (conj alt (str "[" (join cc) "]")))]
          (opt (str (case (count alt)
                      0 ""
                      1 (first alt)
                      (str "(?:" (join "|" alt) ")")))))
        (let [quoted (Pattern/quote (str c))]
          (if (and (.word trie') (empty? (.children trie')))
            (recur children' alt (conj cc quoted))
            (recur children' (conj alt (str quoted (%trie->regexp trie'))) cc)))))))

(defn trie->regexp [trie]
  (re-pattern (%trie->regexp trie)))

(defn make-tokenizer [word-map]
  (let [trie (reduce add-to-trie (make-trie) (keys word-map))
        regexp (trie->regexp trie)]
    #(map word-map (re-seq regexp %))))
