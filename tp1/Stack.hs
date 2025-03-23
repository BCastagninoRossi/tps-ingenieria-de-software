module Stack ( Stack, newS, freeCellsS, stackS, netS, holdsS, popS )
  where

import Palet
import Route


data Stack = Sta [ Palet ] Int deriving (Eq, Show)

newS :: Int -> Stack  
newS capacity | capacity <= 0 = error "Stack capacity must be greater than 0"
              | otherwise = Sta [] capacity

freeCellsS :: Stack -> Int  
freeCellsS (Sta palets capacity) = capacity - length palets

netS :: Stack -> Int  
netS (Sta palets capacity) | palets == [] = 0
                           | otherwise = sum (map netP palets)

stackS :: Stack -> Palet -> Stack  
stackS (Sta palets capacity) palet = Sta (palet : palets) capacity

holdsS :: Stack -> Palet -> Route -> Bool 
holdsS (Sta [] _) _ _ = True
holdsS (Sta palets capacity) palet route =
    freeCellsS (Sta palets capacity) > 0 &&
    netS (Sta palets capacity) + netP palet <= 10 &&
    foldr (\p acc -> inOrderR route (destinationP p) (destinationP palet) && acc) True palets

popS :: Stack -> String -> Stack 
popS (Sta [] capacity) _ = Sta [] capacity
popS (Sta (topPallet:pallets) capacity) destino | destinationP topPallet == destino = popS (Sta pallets capacity) destino
                                                | otherwise = Sta (topPallet:pallets) capacity
