(ns com.kirasystems.util
  (:import java.util.UUID))

(defn uuid
  []
  (.toString (UUID/randomUUID)))
