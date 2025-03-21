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

stackS :: Stack -> Palet -> Stack  -- apila el palet indicado en la pila
stackS (Sta palets capacity) palet  | length palets < capacity && (netS (Sta palets capacity) + netP palet) <= 10 = Sta (palet : palets) capacity
                                    | otherwise = error "Stack is full or exceeds weight limit"



holdsS :: Stack -> Palet -> Route -> Bool  -- indica si la pila puede aceptar el palet considerando las ciudades en la ruta
holdsS (Sta [] _) _ _ = True
holdsS (Sta (topPalet : palets) capacity) palet route =   --- CHEQUEAR
    inOrderR route (destinationP topPalet) (destinationP palet) && (netS (Sta (topPalet : palets) capacity) + netP palet) <= 10




popS :: Stack -> String -> Stack  -- quita los palets con destino en la ciudad indicada
popS (Sta [] capacity) _ = Sta [] capacity
popS (Sta (p:ps) capacity) destino
    | destinationP p == destino = popS (Sta ps capacity) destino
    | otherwise = Sta (p:ps) capacity
-- stack: la pila de la que se quitan palets
-- destino: ciudad en la que se descargan palets