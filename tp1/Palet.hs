module Palet ( Palet, newP, destinationP, netP )
  where

data Palet = Pal String Int deriving (Eq, Show)

newP :: String -> Int -> Palet 
newP destino peso | peso <= 0 = error "Weight must be greater than 0"
                  | otherwise = Pal destino peso

destinationP :: Palet -> String
destinationP (Pal destino _) = destino

netP :: Palet -> Int    
netP (Pal _ peso) = peso
