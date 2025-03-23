module Route ( Route, newR, inOrderR, inRouteR )
   where

data Route = Rou [ String ] deriving (Eq, Show)


newR :: [ String ] -> Route 
newR ciudades = Rou ciudades

inOrderR :: Route -> String -> String -> Bool 
inOrderR (Rou ciudades) ciudad1 ciudad2 = ciudad1 `elem` ciudades && ciudad2 `elem` ciudades && 
                                          foldr (\ciudad acc -> (ciudad == ciudad1) || (ciudad /= ciudad2 && acc)) False ciudades

inRouteR :: Route -> String -> Bool 
inRouteR (Rou ciudades) ciudad = ciudad `elem` ciudades