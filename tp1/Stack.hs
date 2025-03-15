module Stack ( Stack, newS, freeCellsS, stackS, netS, holdsS, popS )
  where

import Palet
import Route

data Stack = Sta [ Palet ] Int deriving (Eq, Show)

newS :: Int -> Stack                      -- construye una Pila con la capacidad indicada (cantidad de pallets maxima)
newS capacity = Sta [] capacity

freeCellsS :: Stack -> Int                -- responde la celdas disponibles en la pila (cantidad de pallets que puede admitir)
freeCellsS (Sta palets capacity) = capacity - length palets

stackS :: Stack -> Palet -> Stack         -- apila el palet indicado en la pila 
stackS (Sta palets capacity) palet
    | length palets < capacity = Sta (palet : palets) capacity
    | otherwise = error "Stack is full"

netS :: Stack -> Int                      -- responde el peso neto de los paletes en la pila (no hay limite)
netS (Sta palets _) = sum $ map netP palets

holdsS :: Stack -> Palet -> Route -> Bool -- indica si la pila puede aceptar el palet considerando las ciudades en la ruta


popS :: Stack -> String -> Stack          -- quita del tope los paletes con destino en la ciudad indicada