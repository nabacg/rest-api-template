(defproject rest-api-starter "0.1.0-SNAPSHOT"
  :description "Template for rest service"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [ring "1.2.0"]
                 [ring-server "0.2.8" :exclsions [[org.clojure/clojure]
                                                  [ring]]]
                 [ring/ring-json "0.2.0"]
                 [com.novemberain/monger "1.7.0"]
                 [compojure "1.1.5" :exclusions [[org.clojure/clojure]
                                                 [ring/ring-core]]]]
  :uberjar-name "rest-api-starter.jar"
  :local-repo "lib"
  :min-lein-version "2.0.0"
  :main rest-api-starter.core)
