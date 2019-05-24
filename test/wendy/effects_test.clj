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

(ns wendy.effects-test
  (:require [clojure.test :refer :all]
            [clj-http.lite.client :as http]
            [wendy.effects :refer :all]))

(deftest helpers-test
  (testing "unsafe! wrapper"
    (is (instance? Exception (unsafe! (/ 5 0)))))
  (testing "failed check"
    (is (true? (failed? (unsafe! (/ 5 0))))))
  (testing "fail-with just message"
    (let [result (fail-with "shit happened")]
      (is (true? (:failed? result)))
      (is (= "shit happened" (:reason result)))
      (is (nil? (:status result)))))
  (testing "fail-wth message and status"
    (let [result (fail-with "remote shit happened" 500)]
      (is (true? (:failed? result)))
      (is (= "remote shit happened" (:reason result)))
      (is (= 500 (:status result))))))

(deftest request-test
  (testing "successful request"
    (with-redefs [http/get (constantly {:body "[1, 2, 3]"})]
      (is (= [1, 2, 3] (request "some url")))))
  (testing "failed request"
    (with-redefs [http/get (constantly (Exception. "failed"))]
      (is (:failed? (request "some url")))))
  (testing "successful post request"
    (with-redefs [http/post (constantly {:body "[1, 2, 3]"})]
      (is (= [1, 2, 3] (request "some url" :post)))))
  (testing "successful delete request"
    (with-redefs [http/delete (constantly {:body "{\"message\": \"Ok\"}"})]
      (is (= {:message "Ok"} (request "some url" :delete))))))
