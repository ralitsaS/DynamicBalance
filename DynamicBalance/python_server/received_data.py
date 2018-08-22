import numpy as np
import pandas as pd
from scipy.stats import mode
import os.path
import os

def preprocess_data(filename):
    df_received = pd.read_csv(filename)
    
    if pd.isnull(df_received['steps_per_min']).iloc[-1]:
        df_received['steps_per_min'].iloc[-1] = 0
    
    df_received['heart_rate'] = df_received['heart_rate'].fillna(method = 'ffill').fillna(method = 'bfill')
    df_received['acceleration'] = df_received['acceleration'].fillna(method = 'ffill').fillna(method = 'bfill')
    df_received['steps_per_min'] = df_received['steps_per_min'].fillna(method = 'bfill').fillna(method = 'ffill')
    df_received['hour'] = df_received['hour'].fillna(method = 'ffill').fillna(method = 'bfill')
    df_received['day'] = df_received['day'].fillna(method = 'ffill').fillna(method = 'bfill')
    df_received['activity'] = df_received['activity'].fillna(method = 'ffill').fillna(method = 'bfill')
    df_received['temp'] = df_received['temp'].fillna(method = 'ffill').fillna(method = 'bfill')
    df_received['humidity'] = df_received['humidity'].fillna(method = 'ffill').fillna(method = 'bfill')
    df_received['clouds'] = df_received['clouds'].fillna(method = 'ffill').fillna(method = 'bfill')
    
    df_received.to_csv(filename, encoding='utf-8', index=False)
    
def aggregate_data(uni_id):
    rd_filename = 'received_data_' + uni_id + '.csv'
    df_data = pd.read_csv(rd_filename)
    interval_indexes = df_data[pd.notnull(df_data['level'])].index 
    if interval_indexes.size == 1:
        df_section = df_data.loc[0:interval_indexes[-1],:]
    else:
        df_section = df_data.loc[interval_indexes[-2]+1:interval_indexes[-1],:]
    
    
    agg_data = []
    agg_data.append({'uni_id': df_section['uni_id'].iloc[-1],'timestamp': df_section['timestamp'].iloc[-1],'meanHR': df_section['heart_rate'].mean(), 'minHR': min(df_section['heart_rate']), 'maxHR': max(df_section['heart_rate']), 'stdHR': df_section['heart_rate'].std(), 'avgchngHR': abs(df_section['heart_rate'].diff()).mean(), 'hour': mode(df_section['hour'])[0][0], 'day': df_section['day'].iloc[-1], 'stepsPerMin': df_section['steps_per_min'].mean(), 'meanActivity': df_section['activity'].mean(), 'modeActivity': mode(df_section['activity'])[0][0], 'changedActivity': (df_section['activity'].diff() != 0).sum()-1, 'meanAccel': df_section['acceleration'].mean(), 'stdAccel': df_section['acceleration'].std(), 'temp': mode(df_section['temp'])[0][0], 'humidity': mode(df_section['humidity'])[0][0], 'clouds': mode(df_section['clouds'])[0][0], 'userHR': df_section['userHR'].iloc[-1], 'userSTD': df_section['userSTD'].iloc[-1], 'level': df_section['level'].iloc[-1]})
    
    df_data_5min = pd.DataFrame(agg_data, columns=['uni_id', 'timestamp', 'meanHR', 'minHR', 'maxHR', 'stdHR', 'avgchngHR', 'hour', 'day', 'stepsPerMin', 'meanActivity', 'modeActivity', 'changedActivity', 'meanAccel', 'stdAccel', 'temp', 'humidity', 'clouds', 'userHR', 'userSTD', 'level'])
    
    df_data_5min = df_data_5min.fillna('0')
    
    df_data = df_data.iloc[-1,:].to_frame().transpose()
    os.remove(rd_filename)
    df_data.to_csv(rd_filename, encoding='utf-8', index=False)
    
    agg_filename = 'agg_data_' + uni_id + '.csv'
    
    ans = "nope"
    
    if not (df_data_5min['stdHR'].iloc[0]=='0'):
        if os.path.exists(agg_filename):
            with open(agg_filename, 'a') as f:
                df_data_5min.to_csv(f, header=False, index=False)
            df_agg_data = pd.read_csv(agg_filename)
            if df_agg_data.shape[0] >= 3:
                df_last_sample = df_agg_data.tail(3)
                make_features(df_last_sample, uni_id)
                ans = df_last_sample['timestamp'].iloc[-1]
            
        else:
            df_data_5min.to_csv(agg_filename, encoding='utf-8', index=False)
        
    return ans

def make_features(df3, uni_id):
    df_3x5min = df3.tail(1)
    
    df_3x5min['meanHR-t'] = df3['meanHR'].iloc[-2]
    df_3x5min['stdHR-t'] = df3['stdHR'].iloc[-2]
    df_3x5min['minHR-t'] = df3['minHR'].iloc[-2]
    df_3x5min['maxHR-t'] = df3['maxHR'].iloc[-2]
    df_3x5min['avgchngHR-t'] = df3['avgchngHR'].iloc[-2]
    df_3x5min['stepsPerMin-t'] = df3['stepsPerMin'].iloc[-2]
    df_3x5min['meanActivity-t'] = df3['meanActivity'].iloc[-2]
    df_3x5min['modeActivity-t'] = df3['modeActivity'].iloc[-2]
    df_3x5min['changedActivity-t'] = df3['changedActivity'].iloc[-2]
    df_3x5min['meanAccel-t'] = df3['meanAccel'].iloc[-2]
    df_3x5min['stdAccel-t'] = df3['stdAccel'].iloc[-2]
    df_3x5min['level-t'] = df3['level'].iloc[-2]
    
    df_3x5min['meanHR-2t'] = df3['meanHR'].iloc[-3]
    df_3x5min['stdHR-2t'] = df3['stdHR'].iloc[-3]
    df_3x5min['minHR-2t'] = df3['minHR'].iloc[-3]
    df_3x5min['maxHR-2t'] = df3['maxHR'].iloc[-3]
    df_3x5min['avgchngHR-2t'] = df3['avgchngHR'].iloc[-3]
    df_3x5min['stepsPerMin-2t'] = df3['stepsPerMin'].iloc[-3]
    df_3x5min['meanActivity-2t'] = df3['meanActivity'].iloc[-3]
    df_3x5min['modeActivity-2t'] = df3['modeActivity'].iloc[-3]
    df_3x5min['changedActivity-2t'] = df3['changedActivity'].iloc[-3]
    df_3x5min['meanAccel-2t'] = df3['meanAccel'].iloc[-3]
    df_3x5min['stdAccel-2t'] = df3['stdAccel'].iloc[-3]
    df_3x5min['level-2t'] = df3['level'].iloc[-3]
    
    features_filename = "features15min_" + uni_id + ".csv"
    
    if os.path.exists(features_filename):
        with open(features_filename, 'a') as f:
            df_3x5min.to_csv(f, header=False, index=False)    
    else:
        df_3x5min.to_csv(features_filename, encoding='utf-8', index=False)
    
    
    

