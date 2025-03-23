module Truck ( Truck, newT, freeCellsT, loadT, unloadT, netT )
  where

import Palet
import Stack
import Route

data Truck = Tru [ Stack ] Route deriving (Eq, Show)

newT :: Int -> Int -> Route -> Truck
newT numBays capacity route
  | numBays <= 0   = error "Number of bays must be greater than 0"
  | capacity <= 0  = error "Capacity must be greater than 0"
  | otherwise      = Tru (foldr (\_ acc -> newS capacity : acc) [] [1..numBays]) route

freeCellsT :: Truck -> Int
freeCellsT (Tru stacks _) = sum (map freeCellsS stacks)


extractIndices :: [(Int, Stack)] -> Palet -> Route -> [Int]
extractIndices [] _ _ = []
extractIndices ((i, st):xs) palet route
  | holdsS st palet route = i : extractIndices xs palet route
  | otherwise             = extractIndices xs palet route


rebuildStacks :: [(Int, Stack)] -> Palet -> Int -> [Stack]
rebuildStacks [] _ _ = []
rebuildStacks ((i, st):xs) palet idx
  | i == idx  = stackS st palet : rebuildStacks xs palet idx
  | otherwise = st : rebuildStacks xs palet idx


loadT :: Truck -> Palet -> Truck
loadT (Tru stacks route) palet
  | freeCellsT (Tru stacks route) == 0                    = error "No free cells available in the truck"
  | not (inRouteR route (destinationP palet))             = error "Destination city not in route"
  | null (extractIndices (zip [0..] stacks) palet route)  = error "No suitable stack found to load the pallet"
  | otherwise =
      Tru
        (rebuildStacks (zip [0..] stacks) palet (head (extractIndices (zip [0..] stacks) palet route)))
        route

unloadT :: Truck -> String -> Truck
unloadT (Tru stacks route) destino =
  Tru (foldr (\stack acc -> popS stack destino : acc) [] stacks) route

netT :: Truck -> Int
netT (Tru stacks _) = sum (map netS stacks)

