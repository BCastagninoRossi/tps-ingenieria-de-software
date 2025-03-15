module Truck ( Truck, newT, freeCellsT, loadT, unloadT, netT )
  where

import Palet
import Stack
import Route
import Data.List (find)

data Truck = Tru [ Stack ] Route deriving (Eq, Show)

newT :: Int -> Int -> Route -> Truck  -- construye un camion según una cantidad de bahias, la altura de las mismas y una ruta
newT numBays capacity route = Tru (replicate numBays (newS capacity)) route

freeCellsT :: Truck -> Int            -- responde la celdas disponibles en el camion
freeCellsT (Tru stacks _) = sum $ map freeCellsS stacks

loadT :: Truck -> Palet -> Truck      -- carga un palet en el camion
loadT (Tru stacks route) palet =
    case findSuitableStack stacks palet route of
        Just (index, stack) -> Tru (replaceStack stacks index (stackS stack palet)) route
        Nothing -> error "No suitable stack available"
  where
    findSuitableStack :: [Stack] -> Palet -> Route -> Maybe (Int, Stack)
    findSuitableStack stacks palet route =
        find (\(i, s) -> holdsS s palet route && freeCellsS s > 0) (zip [0..] stacks)

    replaceStack :: [Stack] -> Int -> Stack -> [Stack]
    replaceStack stacks index newStack =
        take index stacks ++ [newStack] ++ drop (index + 1) stacks

unloadT :: Truck -> String -> Truck   -- responde un camion al que se le han descargado los paletes que podían descargarse en la ciudad
unloadT (Tru stacks route) destino =
    Tru (map (\stack -> popS stack destino) stacks) route

netT :: Truck -> Int                  -- responde el peso neto en toneladas de los paletes en el camion
netT (Tru stacks _) = sum $ map netS stacks