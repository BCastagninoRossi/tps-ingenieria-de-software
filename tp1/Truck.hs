module Truck ( Truck, newT, freeCellsT, loadT, unloadT, netT )
  where

import Palet
import Stack
import Route

data Truck = Tru [ Stack ] Route deriving (Eq, Show)

newT :: Int -> Int -> Route -> Truck  -- construye un camion según una cantidad de bahias, la altura de las mismas y una ruta
newT numBays capacity route | numBays <= 0 = error "Number of bays must be greater than 0"
                            | capacity <= 0 = error "Capacity must be greater than 0"
                            | otherwise = Tru (foldr (\_ acc -> newS capacity : acc) [] [1..numBays]) route


freeCellsT :: Truck -> Int            -- responde la celdas disponibles en el camion
freeCellsT (Tru stacks _) = sum $ map freeCellsS stacks

loadT :: Truck -> Palet -> Truck
loadT (Tru stacks route) palet  | freeCellsT (Tru stacks route) == 0 = error "No free cells available in the truck"
                                | not (inRouteR route (destinationP palet)) = error "Destination city not in route"
                                | otherwise =
                                    let availableIndices = foldr (\(i, stack) acc -> if holdsS stack palet route then i : acc else acc) [] (zip [0..] stacks)
                                    in if availableIndices == []
                                        then error "No suitable stack found to load the pallet"
                                    else let newStacks = foldr (\(i, stack) acc -> if i == head availableIndices then stackS stack palet : acc else stack : acc) [] (zip [0..] stacks)
                                        in Tru newStacks route

unloadT :: Truck -> String -> Truck   -- responde un camion al que se le han descargado los paletes que podían descargarse en la ciudad
unloadT (Tru stacks route) destino = Tru (foldr (\stack acc -> popS stack destino : acc) [] stacks) route

netT :: Truck -> Int                  -- responde el peso neto en toneladas de los paletes en el camion
netT (Tru stacks _) = sum $ map netS stacks