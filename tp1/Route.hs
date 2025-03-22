module Route ( Route, newR, inOrderR, inRouteR )
   where

data Route = Rou [ String ] deriving (Eq, Show)


newR :: [ String ] -> Route -- construye una ruta según una lista de ciudades
newR ciudades = Rou ciudades

inOrderR :: Route -> String -> String -> Bool -- indica si la primer ciudad consultada está antes que la segunda ciudad en la ruta
inOrderR (Rou ciudades) ciudad1 ciudad2 =
    ciudad1 `elem` ciudades && ciudad2 `elem` ciudades &&
    foldr (\ciudad acc -> if ciudad == ciudad2 then False else ciudad == ciudad1 || acc) False ciudades

inRouteR :: Route -> String -> Bool -- indica si la ciudad consultada está en la ruta
inRouteR (Rou ciudades) ciudad = ciudad `elem` ciudades