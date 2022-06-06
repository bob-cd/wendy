(require '[clojure.test :as t]
         '[babashka.classpath :as cp])

(cp/add-classpath "src:test")

(def test-nses
  ['wendy.client-test])

(apply require test-nses)

(def test-results
  (apply t/run-tests test-nses))

(def failures-and-errors
  (let [{:keys [:fail :error]} test-results]
    (+ fail error)))

(System/exit failures-and-errors)
