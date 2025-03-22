module Stack ( Stack, newS, freeCellsS, stackS, netS, holdsS, popS )
  where

import Palet
import Route


data Stack = Sta [ Palet ] Int deriving (Eq, Show)

newS :: Int -> Stack  -- construye una Pila con la capacidad indicada
newS capacity | capacity <= 0 = error "Stack capacity must be greater than 0"
              | otherwise = Sta [] capacity

freeCellsS :: Stack -> Int  -- responde las celdas disponibles en la pila
freeCellsS (Sta palets capacity) = capacity - length palets


netS :: Stack -> Int  -- responde el peso neto de los palets en la pila
netS (Sta palets capacity) | palets == [] = 0
                           | otherwise = sum (map netP palets)

stackS :: Stack -> Palet -> Stack  -- apila el palet indicado en la pila. siempre llamar a holdsS antes
stackS (Sta palets capacity) palet = Sta (palet : palets) capacity
                                   



holdsS :: Stack -> Palet -> Route -> Bool  -- indica si la pila puede aceptar el palet considerando las ciudades en la ruta
holdsS (Sta [] _) _ _ = True
holdsS (Sta palets capacity) palet route =
    freeCellsS (Sta palets capacity) > 0 &&
    netS (Sta palets capacity) + netP palet <= 10 &&
    foldr (\p acc -> inOrderR route (destinationP p) (destinationP palet) && acc) True palets


popS :: Stack -> String -> Stack  -- quita los palets con destino en la ciudad indicada (solo si están en la parte de arriba de la pila)
popS (Sta [] capacity) _ = Sta [] capacity
popS (Sta (topPallet:pallets) capacity) destino | destinationP topPallet == destino = popS (Sta pallets capacity) destino
                                                | otherwise = Sta (topPallet:pallets) capacity
