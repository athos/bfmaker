(ns bfmaker.interp
  (:use [bfmaker.interp.tokenizer :only [make-tokenizer]]
        [bfmaker.interp.vm :only [parse execute]]))

(defn make-interpreter [word-map memory pointer]
  (let [tokenizer (make-tokenizer word-map)]
    (fn [program]
      (let [insn (tokenizer program)
            [code rest] (parse insn)]
        (when rest
          (throw (Exception. "unbalanced ['s and ]'s")))
        (execute code memory pointer)))))
