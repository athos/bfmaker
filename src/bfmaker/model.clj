(ns bfmaker.model
  (:use [somnium.congomongo]))

(defn fetch-lang [lang-id]
  {"+" :inc, "-" :dec, ">" :forth, "<" :back
   "[" :while, "]" :end-while, "." :write, "," :read})

(defn fetch-code [code-id]
  nil)

(defn fetch-code-since [since-id]
  nil)
