import pandas as pd
import numpy as np
from sklearn.model_selection import train_test_split
from sklearn import preprocessing
from sklearn import linear_model
from sklearn import neural_network
from sklearn.metrics import accuracy_score
from sklearn.externals import joblib
import os.path
import os

def make_y(df_feedback, df_report):
    for ts in df_report['timestamp']:
        df_feedback['y'].iloc[(df_feedback['timestamp'].astype('int64')-long(ts)).abs().argsort()[:3]] = '1'
        print df_report['context'].loc[df_report['timestamp'].astype('str')==ts]
        df_feedback['context'].iloc[(df_feedback['timestamp'].astype('int64')-long(ts)).abs().argsort()[:3]] = df_report['context'].loc[df_report['timestamp'].astype('str')==ts].iloc[0]
    
    
    return df_feedback
    
    
    
def retrain_model(uni_id, df_feedback, df_report):
    model_filename = "model_" + uni_id + ".sav"
    scaler_filename = "scaler_" + uni_id + ".sav"
    features_filename = "features15min_" + uni_id + ".csv"
    feedback_filename = "feedback_" + uni_id + ".csv"
    
    if os.path.exists(model_filename):
        print "uni model exists"
        model = joblib.load(model_filename) 
        scaler = joblib.load(scaler_filename)
    else:
        print "uni model doesnt exist"
        model = neural_network.MLPClassifier(solver="sgd", learning_rate="adaptive")
        scaler = preprocessing.StandardScaler()
        
    df_y = make_y(df_feedback, df_report)
    begin_ts = df_y['timestamp'].iloc[0]
    
    df_features_data = pd.read_csv(features_filename)
    
    print begin_ts
    
    start_index = df_features_data.loc[df_features_data['timestamp'].astype('int64').astype('str')==begin_ts].index[0]
    
    
    #
    train_y = df_y['y'].astype('int')
    train_X = df_features_data.iloc[start_index:,:]
    
    print str(train_y.size)
    print str(train_X.shape[0])
    
    if not (train_y.size==train_X.shape[0]):
        for ts in train_X['timestamp']:
            if not (str(int(ts)) in df_y['timestamp'].values):
                print "missing timestamp: "+str(ts)
                train_X = train_X.drop(train_X.loc[train_X['timestamp'].astype('int64').astype('str')==str(int(ts))].index[0])
    
            
    train_X = train_X.drop('timestamp', 1)
    train_X = train_X.drop('uni_id', 1)
    train_X = train_X.drop('userHR',1)
    train_X = train_X.drop('userSTD',1)
    train_X = train_X.drop('stdAccel-t',1)
    train_X = train_X.drop('avgchngHR-2t',1)
    train_X = train_X.drop('avgchngHR-t',1)
    train_X = train_X.drop('avgchngHR',1)
    train_X = train_X.drop('minHR',1)
    train_X = train_X.drop('stdAccel',1)
    train_X = train_X.drop('minHR-t',1)
    train_X = train_X.drop('meanAccel-t',1)
    train_X = train_X.drop('minHR-2t',1)
    train_X = train_X.drop('meanAccel-2t',1)
    train_X = train_X.drop('stdAccel-2t',1)
    
    scaler.partial_fit(train_X)
    train_X = scaler.transform(train_X)
    model.partial_fit(train_X, train_y, classes=[0,1])
    
    joblib.dump(model, model_filename)
    joblib.dump(scaler, scaler_filename)
    print "dump model"
    df_feedback = pd.read_csv(feedback_filename)
    
    df_feedback['epoch'].loc[df_feedback['timestamp'].astype('int64').astype('str')==begin_ts] = "new"
    
    for ts in df_y['timestamp']:
        df_feedback['y'].loc[df_feedback['timestamp'].astype('int64').astype('str')==ts] = df_y['y'].loc[df_y['timestamp'].astype('int64').astype('str')==ts].iloc[0]
        df_feedback['context'].loc[df_feedback['timestamp'].astype('int64').astype('str')==ts] = df_y['context'].loc[df_y['timestamp'].astype('int64').astype('str')==ts].iloc[0]
        
    df_feedback.to_csv(feedback_filename, encoding='utf-8', index=False)
    print "save feedback"
    
    agg_filename = 'agg_data_' + uni_id + '.csv'
    os.remove(agg_filename)
    
    
def get_prediction(uni_id, timestamp):
    model_filename = "model_" + uni_id + ".sav"
    scaler_filename = "scaler_" + uni_id + ".sav"
    features_filename = "features15min_" + uni_id + ".csv"
    feedback_filename = "feedback_" + uni_id + ".csv"
    
    if os.path.exists(model_filename):
         model = joblib.load(model_filename) 
         scaler = joblib.load(scaler_filename)
    else:
        model = joblib.load("model_init.sav")
        scaler = joblib.load("scaler_init.sav")
        
    df_features = pd.read_csv(features_filename)
    test_X = df_features.loc[df_features['timestamp']==timestamp]
    test_X = test_X.drop('timestamp', 1)
    test_X = test_X.drop('uni_id', 1)
    test_X = test_X.drop('userHR',1)
    test_X = test_X.drop('userSTD',1)
    test_X = test_X.drop('stdAccel-t',1)
    test_X = test_X.drop('avgchngHR-2t',1)
    test_X = test_X.drop('avgchngHR-t',1)
    test_X = test_X.drop('avgchngHR',1)
    test_X = test_X.drop('minHR',1)
    test_X = test_X.drop('stdAccel',1)
    test_X = test_X.drop('minHR-t',1)
    test_X = test_X.drop('meanAccel-t',1)
    test_X = test_X.drop('minHR-2t',1)
    test_X = test_X.drop('meanAccel-2t',1)
    test_X = test_X.drop('stdAccel-2t',1)
    
    test_X = scaler.transform(test_X)
    prediction = model.predict(test_X)[0]
    proba = model.predict_proba(test_X)[0][1]
    
    d = {'timestamp': timestamp, 'prediction': prediction, 'probability': proba, 'y': None, 'context': None, 'epoch':None}
    #df = pd.DataFrame(d.items(), columns=['timestamp', 'prediction', 'y', 'context'])
    df = pd.Series(d, index=['timestamp', 'prediction', 'probability', 'y', 'context', 'epoch']).to_frame().transpose()
    
    if os.path.exists(feedback_filename):
        with open(feedback_filename, 'a') as f:
            df.to_csv(f, header=False, index=False) 
    else:
        df.to_csv(feedback_filename, encoding='utf-8', index=False)
        
    return prediction
    
    