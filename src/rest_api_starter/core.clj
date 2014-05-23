(ns rest-api-starter.core
  (:use [ring.middleware.reload]
        [ring.util.response])
  (:require [compojure.handler :as handler]
            [compojure.core
             :as c-core
             :refer [defroutes GET POST PUT DELETE HEAD OPTIONS PATCH ANY]]
            [compojure.route :as c-route]
            [ring.server.standalone :as server]
            [ring.middleware.json :as ring-json]
            [monger.core :as mg]
            [monger.collection :as mc])
  (:import [com.mongodb MongoOptions ServerAddress]))

(def db (atom {:woz [:name "Steve Wozniak" :email "woz@apple.com"]
               :jobs [:name "Jobs" :email "jobs@apple.com"]}))

;to start mongodb run below line in terminal
;:~/DEV/mondodb/mongodb-linux-x86_64-2.4.9 ./bin/mongod --dbpath
                                        ;~/DEV/mondodb/data/
(def mongo-conn-uri "mongodb://rest-api-db-user:SubSecret@oceanic.mongohq.com:10095/app25545053")
;; mongodb://rest-api-db-user:SubSecret@oceanic.mongohq.com:10095/app25545053
(let [mongo-uri "mongodb://rest-api-db-user:SubSecret@oceanic.mongohq.com:10095/app25545053"
      {:keys [conn db]} (mg/connect-via-uri mongo-uri)]
   (mc/insert-batch db "entries" [{:_id "woz" :name "Steve Wozniak" :email "woz@apple.com"}
                            {:_id "jobs" :name "Jobs" :email "jobs@apple.com"}]))

(defn connect-and-run [conn-uri cmd]
  (let [mongo-uri conn-uri
      {:keys [conn db]} (mg/connect-via-uri mongo-uri)]
    (cmd db)))

(connect-and-run mongo-conn-uri
                 (fn [db] (mc/insert-batch db "entries" [{:_id "woz" :name "Steve Wozniak" :email "woz@apple.com"}
                            {:_id "jobs" :name "Jobs" :email "jobs@apple.com"}])))

;(def mongo-db (mg/get-db "userdb"))
;(mg/set-db! mongo-db)



;;(mc/remove "entries")
;; (mc/remove "entries" {:name "Jobs"})

(defroutes api
  (GET "/" [] (slurp "resources/public/html/index.html"))
  ;; (PUT "/entry" {{name :name :as user}  :body}  (response (swap! db assoc (keyword name) user)))
  ;; (GET "/entries" [] (response @db))
 ;; (GET "/entry/:name" [name] (response (@db (keyword name))))
  
  (PUT "/entry" {{name :name email :email} :body}
       (response
        (connect-and-run mongo-conn-uri
                         (fn [db] 
                           (do
                             (mc/insert db "entries" {:_id email :name name :email email })
                             (mc/find-maps db "entries"))))))
  (GET "/entry/:name" [name] (response
                              (connect-and-run mongo-conn-uri
                                               (fn [db] (mc/find-maps db
                                                                     "entries" {:name name})))))
  (GET "/entries" [] (response
                      (connect-and-run mongo-conn-uri (fn [db]  (mc/find-maps db "entries")))))
  (GET "/ping" [] (response {:msg "PONG"}))
  (c-route/resources "/"))


(def app
  (-> (var api)
      (handler/api)
      (wrap-reload '(rest-api-starter.core))
      (ring-json/wrap-json-body {:keywords? true})
      (ring-json/wrap-json-response)))

(defn start-server [port]
  (server/serve #'app
                {:port port
                 :join? false
                 :open-browser? false}))

;(def server (start-server 8899))

(defn -main [port]
  (start-server (Integer. port)))

                                        ;(mg/connect!)

;; (mg/set-db! (mg/get-db "restdb"))

;; (mc/insert "users" {:name "steve" :email "steve@steve.com"})

;; (mc/insert-batch "users" [{ :name "Steve Wozniak" :email "woz@apple.com"}
;;                           {:name "Jobs" :email "jobs@apple.com"}])

;; (mc/find "users") ;returns some java dbcursor object


;; (mc/find-maps "users") ;returns proper Clojure map

;; (mc/find-maps "users" {:name "steve"})

;; (doseq [u (mc/find-maps "users")]
;;   (println (:name u)))