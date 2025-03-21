module Route ( Route, newR, inOrderR, inRouteR )
   where

data Route = Rou [ String ] deriving (Eq, Show)


newR :: [ String ] -> Route  -- construye una ruta según una lista de ciudades
newR = Rou
-- ciudades: lista de ciudades que conforman la ruta


inOrderR :: Route -> String -> String -> Bool  -- indica si la primer ciudad consultada está antes que la segunda
inOrderR (Rou ciudades) ciudad1 ciudad2 =
    ciudad1 `elem` ciudades && ciudad2 `elem` ciudades &&  -- Verifica si ambas ciudades están en la ruta
    foldr (\ciudad acc -> (ciudad == ciudad1) || (ciudad /= ciudad2 && acc)) False ciudades
    -- Recorre la lista de ciudades:
    -- Si encuentra ciudad1, retorna True
    -- Si encuentra ciudad2 antes que ciudad1, retorna False
    -- Si ninguna de las dos condiciones se cumple, continúa recorriendo la lista



inRouteR :: Route -> String -> Bool  -- indica si la ciudad consultada está en la ruta
inRouteR (Rou ciudades) ciudad = ciudad `elem` ciudades
-- route: la ruta con sus ciudades
-- ciudad: ciudad a consultar


