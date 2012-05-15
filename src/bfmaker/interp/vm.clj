(ns bfmaker.interp.vm)

;; insn list -> (insn tree, insn list)
(defn parse
  ([insns] (parse insns []))
  ([[insn & rest :as insns] result]
   (if (empty? insns)
     [result insns]
     (case insn
       :while (let [[result' insns'] (parse rest [])]
                (recur insns' (conj result result')))
       :end-while [result rest]
       (recur rest (conj result insn))))))

(defmulti do-insn (fn [insn _ _] insn))

(defmethod do-insn :inc [insn memory pointer]
  [(assoc memory pointer (inc (memory pointer))) pointer])

(defmethod do-insn :dec [insn memory pointer]
  [(assoc memory pointer (dec (memory pointer))) pointer])

(defmethod do-insn :forth [insn memory pointer]
  [memory (inc pointer)])

(defmethod do-insn :back [insn memory pointer]
  [memory (dec pointer)])

(defmethod do-insn :write [insn memory pointer]
  (print (char (memory pointer)))
  (flush)
  [memory pointer])

(defmethod do-insn :read [insn memory pointer]
  (let [c (.read *in*)]
    [(assoc memory pointer c) pointer]))

(defn execute
  ([code memory pointer] (execute code code memory pointer false))
  ([[insn & rest :as code] code0 memory pointer loop?]
   (cond (empty? code)
         (if (and loop? (not= (memory pointer) 0))
           (recur code0 code0 memory pointer loop?)
           [memory pointer])

         (coll? insn)
         (if (= (memory pointer) 0)
           (recur rest code0 memory pointer loop?)
           (let [[memory' pointer'] (execute insn insn memory pointer true)]
             (recur rest code0 memory' pointer' loop?)))

         :else
         (let [[memory' pointer'] (do-insn insn memory pointer)]
           (recur rest code0 memory' pointer' loop?)))))
