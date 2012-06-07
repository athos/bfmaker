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

(def +max-value+ 65535)

(defn vm-error [msg]
  (throw (IllegalStateException. msg)))

(defmulti do-insn (fn [insn _ _] insn))

(defmethod do-insn :inc [insn memory pointer]
  (let [v (inc (memory pointer))]
    (when (> v +max-value+)
      (vm-error (str "overflow at " pointer)))
    [(assoc memory pointer v) pointer]))

(defmethod do-insn :dec [insn memory pointer]
  (let [v (dec (memory pointer))]
    (when (neg? v)
      (vm-error (str "underflow at " pointer)))
    [(assoc memory pointer v) pointer]))

(defmethod do-insn :forth [insn memory pointer]
  (let [p (inc pointer)]
    (when (>= p (count memory))
      (vm-error (str "pointer overflow")))
    [memory p]))

(defmethod do-insn :back [insn memory pointer]
  (let [p (dec pointer)]
    (when (neg? p)
      (vm-error "pointer underflow"))
    [memory p]))

(defmethod do-insn :write [insn memory pointer]
  (print (char (memory pointer)))
  (flush)
  [memory pointer])

(defmethod do-insn :read [insn memory pointer]
  (let [c (.read *in*)]
    [(assoc memory pointer (if (neg? c) 0 c)) pointer]))

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
