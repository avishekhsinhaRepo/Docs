import pandas as pd
from sklearn.linear_model import LogisticRegression

# data = pd.read_csv("./resources/Iris.csv")
# print(data.head())
# print(data.shape)
# specific_data= data[["Id","Species"]]
# print(specific_data.head())

data = pd.read_csv("./resources/Iris.csv")
X= data.drop(columns=['Id','Species'])
Y=data['Species']
print (X.head)
print(Y.head)

ml_model=LogisticRegression()
ml_model.fit(X.values, Y)
ml_predictions=ml_model.predict([[6.4,2.5,0.5,0.1]])
print(ml_predictions)
