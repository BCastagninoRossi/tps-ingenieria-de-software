module Truck ( Truck, newT, freeCellsT, loadT, unloadT, netT )
  where

import Palet
import Stack
import Route

data Truck = Tru [ Stack ] Route deriving (Eq, Show)

newT :: Int -> Int -> Route -> Truck  -- construye un camion según una cantidad de bahias, la altura de las mismas y una ruta
newT numBays capacity route = Tru (replicate numBays (newS capacity)) route
-- numBays: cantidad de bahías
-- capacity: altura de las bahías
-- route: ruta del camión
-- Tru: constructor de Truck
-- replicate: crea una lista con la cantidad de elementos indicada, cada uno con el valor indicado
-- newS: constructor de Stack
-- capacity: cantidad de pallets maxima
-- newS capacity: crea una pila con la capacidad indicada
--- Basicamente crea un camión (Truck) con una cantidad de bahias (Stack) y una ruta (Route) dada

freeCellsT :: Truck -> Int  -- responde la celdas disponibles en el camion
freeCellsT (Tru stacks _) = sum $ map freeCellsS stacks
-- truck: camión del que se consultan las celdas libres

tryLoad :: [Stack] -> Palet -> Route -> [Stack]
tryLoad [] _ _ = error "No hay espacio disponible"
tryLoad (stack:stacks) palet route
    | freeCellsS stack >= netP palet = (stackS stack palet) : stacks
    | otherwise = stack : tryLoad stacks palet route

loadT :: Truck -> Palet -> Truck  -- carga un palet en el camión
loadT (Tru stacks route) palet = Tru (tryLoad stacks palet route) route
-- truck: camión sobre el que se carga
-- palet: palet que se desea cargar

unloadT :: Truck -> String -> Truck  -- responde un camión al que se le han descargado los palets con el destino indicado
unloadT (Tru stacks route) destino =
    Tru (map (\stack -> popS stack destino) stacks) route
-- truck: camión del que se descargan palets
-- destino: ciudad para la que se descargan palets

netT :: Truck -> Int  -- responde el peso neto en toneladas de los palets en el camión
netT (Tru stacks _) = sum $ map netS stacks
-- truck: camión cuyos palets se suman