import numpy as np

def isSym(A, N):
	B= np.transpose(A)
	for i in range(N):
		for j in range(N):
			if (A[i][j] != B[i][j]):
				return False
	return True

N = int(input("Size of Matrix: \n"))

print("Enter the values in line by line")

matrix = []
for i in range(N): 
    a =[] 
    for j in range(N):
        a.append(int(input())) 
    matrix.append(a) 

A = np.array(matrix)
print(A)

if (isSym(A, N)):
	print("Given matrix is Symmetric")	
	evals, evecs = np.linalg.eig(A)
	evecsT = np.transpose(evecs)
	ATA = evecsT.dot(evecs)
	i=0
	while(i<N):
		for j in range(N):
			if(i != j and abs(ATA[i][j]) >= 0.0001):
				print("Eigenvectors are not orthogonal")
				i=N-1
			elif (i == j and abs(ATA[i][j] - 1) >= 0.0001):
				print("Eigenvectors are not orthogonal")
				i=N-1
		i=i+1
	print("Eigenvectors are orthogonal")

else:
	print("Given matrix is Asymmetric")