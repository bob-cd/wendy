;   This file is part of Wendy.
;
;   Wendy is free software: you can redistribute it and/or modify
;   it under the terms of the GNU General Public License as published by
;   the Free Software Foundation, either version 3 of the License, or
;   (at your option) any later version.
;
;   Wendy is distributed in the hope that it will be useful,
;   but WITHOUT ANY WARRANTY; without even the implied warranty of
;   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
;   GNU General Public License for more details.
;
;   You should have received a copy of the GNU General Public License
;   along with Wendy. If not, see <http://www.gnu.org/licenses/>.

(ns wendy.utils-test
  (:require [clojure.test :refer :all]
            [wendy.utils :refer :all]))

(deftest test-map-to-query-str
  (testing "Testing with all paramteres"
    (let [arg {:group "dev" :name "test"}
          result (map-to-query-str arg)]
      (is (= result "?group=dev&name=test"))))
  (testing "Testing with all some null parameters"
    (let [arg {:group "dev" :name nil}
          result (map-to-query-str arg)]
      (is (= result "?group=dev"))))
  (testing "Testing with all nil"
    (let [arg {:group nil :name nil}
          result (map-to-query-str arg)]
      (is (= result ""))))
  (testing "Testing query encoding"
    (let [arg {:group "some text"}
          result (map-to-query-str arg)]
      (is (= result "?group=some+text")))))
