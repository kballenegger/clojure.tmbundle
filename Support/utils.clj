(in-ns 'textmate)
(clojure.core/refer 'clojure.core)
(require '[clojure.string :as string])
(require '[clojure.java.io :as io])

(defonce *compiled-files* (atom #{}))

(defn filepath->ns-str [path]
  (-> path
      (string/replace ".clj" "")
      (string/replace "_" "-")
      (string/replace "/" ".")))

(defn enter-file-ns 
  ([] (enter-file-ns false))
  ([ensure-loaded]
    (let [tm-filepath (bake/*env* "TM_FILEPATH")]
      (if (or (not ensure-loaded) (some @*compiled-files* #{tm-filepath}))
        (let [user-dir (str (System/getProperty "user.dir") "/src/")
              path-to-file (string/replace tm-filepath user-dir "")]
          (in-ns (symbol (filepath->ns-str path-to-file))))
        (in-ns 'user)))))

(defmacro eval-in-file-ns 
  "evaluates forms as though in file namespace or user if 
   file doesn't have explicit namespace"
  [& forms]
  `(do (enter-file-ns)
      ~forms))        
        
(defn carret-info 
  "returns [path line-index column-index] info
   about current location of cursor"
  []
  [(bake/*env* "TM_FILEPATH")
    (dec (Integer/parseInt (bake/*env* "TM_LINE_NUMBER")))
    (Integer/parseInt (bake/*env* "TM_LINE_INDEX"))])      

(defn text-before-carret []
  (let [[path,line-index,column-index] (carret-info)
        lines (-> path io/reader line-seq)]
     (apply str 
       (apply str (for [l (take line-index lines)] (str l "\n")))
       (.substring #^String (nth lines line-index) 0 column-index))))    
        
        
(defn get-last-sexpr [])
(defn get-enclosing-sexpr [])
(defn get-current-symbol [])