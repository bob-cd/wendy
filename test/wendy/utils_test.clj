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
    (let [arg    {:group "dev"
                  :name  "test"}
          result (map-to-query-str arg)]
      (is (= result "?group=dev&name=test"))))
  (testing "Testing with all some null parameters"
    (let [arg    {:group "dev"
                  :name  nil}
          result (map-to-query-str arg)]
      (is (= result "?group=dev"))))
  (testing "Testing with all nil"
    (let [arg    {:group nil
                  :name  nil}
          result (map-to-query-str arg)]
      (is (= result ""))))
  (testing "Testing query encoding"
    (let [arg    {:group "some text"}
          result (map-to-query-str arg)]
      (is (= result "?group=some+text")))))

(deftest test-interpolate
  (testing "interpolate missing params"
    (is (= "a/a-id/path/to/{something-else}/and/{xid}/{not-this}"
           (interpolate-path "a/{id}/path/to/{something-else}/and/{xid}/{not-this}"
                             {:id "a-id"}))))
  (testing "Successful interpolation"
    (is (= "a/a-id/path/to/stuff/and/b-id/{not-this}"
           (interpolate-path "a/{id}/path/to/{something-else}/and/{xid}/{not-this}"
                             {:id             "a-id"
                              :xid            "b-id"
                              :something-else "stuff"})))
    (is (= "a-id/a-id/path/to/stuff/and/b-id/{not-this}"
           (interpolate-path "{id}/{id}/path/to/{something-else}/and/{xid}/{not-this}"
                             {:id             "a-id"
                              :xid            "b-id"
                              :something-else "stuff"}))))

  (testing "Interpolate artifact fetch path"
    (is
      (=
        "/pipelines/groups/dev/names/test/runs/r-14933e85-4196-4457-acc2-5812430d7a55/artifact-stores/local/artifact/app"
        (interpolate-path
          "/pipelines/groups/{group}/names/{name}/runs/{id}/artifact-stores/{store-name}/artifact/{artifact-name}"
          {:group         "dev"
           :name          "test"
           :id            "r-14933e85-4196-4457-acc2-5812430d7a55"
           :store-name    "local"
           :artifact-name "app"})))))
