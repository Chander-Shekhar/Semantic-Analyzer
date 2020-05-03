import numpy as np

def getInput():
	m = int(input()) 
	n = int(input()) 

	A = []
	for i in range(m):
		x = input()
		a = x.split()
		for j in range(n): 
			a[j] = float(a[j])
		A.append(a)

	A = np.array(A)

	y = input()
	B = y.split()
	for j in range(m):
		B[j] = float(B[j])
	B = np.array(B)

	y = input()
	C = y.split()
	for j in range(n):
		C[j] = float(C[j])
	C = np.array(C)

	for i in range(n):
		temp = np.zeros(n)
		temp[i] = -1
		if np.where(np.all(A==temp, axis=1))[0].size == 0:
			A = np.append(A, np.array([temp]), 0)
			B = np.append(B, 0)
	return A, B, C

def getInitialFeasiblePoint(A, B, C):
	allNonNegative = True
	for i in B:
		if i < 0:
			allNonNegative = False;
			break
	if allNonNegative:
		return np.zeros(C.shape[0])
	m = A.shape[0]
	n = A.shape[1]
	for i in range(100*m*m):
		randomRows = np.random.choice(m, n)
		Am = A[randomRows]
		Bm = B[randomRows]
		if np.linalg.matrix_rank(Am) != Am.shape[0]:
			continue
		possiblePoint = np.linalg.inv(Am).dot(Bm)
		tightRows = []
		satisfies = True
		for i in range(A.shape[0]):
			if abs(A[i].dot(possiblePoint) - B[i]) < 0.000000001:
				tightRows.append(i)
				continue
			if A[i].dot(possiblePoint) > B[i]:
				satisfies = False
				break
		if len(tightRows) < n:
			satisfies = False
		if satisfies:
			return possiblePoint
	print("infeasible LP")

def simplex(A, B, C):

	currentPoint = getInitialFeasiblePoint(A, B, C)
	
	while True:
		tightRows = []
		for i in range(A.shape[0]):
			if len(tightRows) == C.shape[0]:
				break
			if abs(A[i].dot(currentPoint) - B[i]) < 0.000000001:
				tightRows.append(i)

		Ap = A[tightRows]

		alphas = C.dot(np.linalg.inv(Ap))
		allPositive = True
		negativeIndex = None
		for i in range(alphas.shape[0]):
			if alphas[i] < 0:
				negativeIndex = i
				allPositive = False
				break
		if allPositive:
			return currentPoint
		directionVector = -np.linalg.inv(Ap)[:, negativeIndex]
		minT = None
		for i in range(A.shape[0]):
			if i in tightRows:
				continue
			if (A[i].dot(directionVector)) <= 0:
				continue
			curT = (B[i] - A[i].dot(currentPoint))/(A[i].dot(directionVector))
			if minT is None or curT < minT:
				minT = curT
		currentPoint = currentPoint + directionVector*minT


A, B, C = getInput()
solution = simplex(A, B, C)
print(solution)