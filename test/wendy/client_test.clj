(ns wendy.client-test
  (:require
    [clojure.test :as t]
    [wendy.client :as client]))

(t/deftest making-request-args
  (t/testing "make a lookup for args"
    (t/is (= {:foo "path" :bar "query"}
             (client/args-lookup [{:ignore 42 :option "foo" :in "path"}
                                  {:also-ignore 66 :option "bar" :in "query"}]))))

  (t/testing "interpolating path"
    (t/is (= "/a/{w}/b/41/42"
             (client/interpolate-path "/a/{w}/b/{x}/{y}" {:x 41 :y 42 :z 43}))))

  (t/testing "making query params"
    (t/is (= {:b 12}
             (client/make-query-params {:a 10 :b 12} {:a "path" :b "query"})))))
