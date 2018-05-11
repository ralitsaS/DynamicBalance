import pandas as pd
import numpy as np
from sklearn.model_selection import train_test_split
from sklearn import preprocessing
from sklearn import linear_model
from sklearn.metrics import accuracy_score
from sklearn.externals import joblib

def make_y(df_feedback, df_report):
    for ts in df_report['timestamp']:
        df_feedback['y'].iloc[(df_feedback['timestamp']-ts).abs().argsort()[:2]] = 1
        df_feedback['context'].iloc[(df_feedback['timestamp']-ts).abs().argsort()[:2]] = df_report['context'].loc[df_report['timestamp']==ts]
    
    print "in make_y"
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
        model = joblib.load("model_init.sav")
        scaler = joblib.load("scaler_init.sav")
        
    df_y = make_y(df_feedback, df_report)
    begin_ts = df_y['timestamp'].iloc[0]
    
    df_features_data = pd.read_csv(features_filename)
    start_index = df_features_data.loc[df_features_data['timestamp']==begin_ts].index[0]
    
    #
    train_y = df_y['y']
    train_X = df_features_data.iloc[start_index:-1,:]
    train_X = train_X.drop('timestamp', 1)
    train_X = train_X.drop('uni_id', 1)
    
    scaler.partial_fit(train_X)
    train_X = scaler.transform(train_X)
    model.partial_fit(train_X, train_y)
    print "partial fit"
    joblib.dump(model, model_filename)
    joblib.dump(scaler, scaler_filename)
    print "dump model"
    df_feedback = pd.read_csv(feedback_filename)
    for ts in df_y['timestamp']:
        df_feedback['y'].loc[df_feedback['timestamp']==ts] = df_y['y'].loc[df_y['timestamp']==ts]
        df_feedback['context'].loc[df_feedback['timestamp']==ts] = df_y['context'].loc[df_y['timestamp']==ts]
        
    df_feedback.to_csv(feedback_filename, encoding='utf-8', index=False)
    print "save feedback"
    
    
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
    
    test_X = scaler.transform(test_X)
    prediction = model.predict(test_X)
    d = {'timestamp': timestamp, 'prediction': prediction, 'y': null, 'context': null}
    df = pd.DataFrame(d)
    
    if os.path.exists(feedback_filename):
        with open(feedback_filename, 'a') as f:
            df.to_csv(f, header=False, index=False) 
    else:
        df.to_csv(feedback_filename, encoding='utf-8', index=False)
        
    return prediction
    
    