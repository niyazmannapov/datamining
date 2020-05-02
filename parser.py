import  urllib
from urllib.request import urlopen
from bs4 import BeautifulSoup
import nltk
from nltk.corpus import stopwords
from nltk.stem import WordNetLemmatizer 
import pandas as pd
from nltk.tokenize import RegexpTokenizer
import sys
import psycopg2
from tqdm import tqdm

tokenizer = RegexpTokenizer(r'\w+')


stop_list = [',', '.', '\'', '\"', "\n", '-', '–', '...', '[', ']', '(', ')', '{', '}', ':', ';',
                 '?', '!', '#', 'i', 'i\'m', 'the', 'a', 'do', 'be', 'and', 'or', 'that', 'these', 'those', 'o', 'not', '1', '2'
            ,'3', '4', '5', '6', '7', '8', '9', '0']


def links_from_acc(soup):
    links = []
    for link in soup.findAll('a'):
        try:
            curr_l = link.get('href')
            if not (curr_l is None) and (len(curr_l) > 1) and ('/' in curr_l):
                curr = curr_l.split('/')
                if (len(curr) == 2) and curr_l[1].isupper():
                    links.append('https://twitter.com' + curr_l)
        except:
            print('exception' + 'in' + curr_l)
    return set(links)


def get_tweets(url):
    tweets = []
    page = urlopen(url)
    soup = BeautifulSoup(page,'html.parser')
    count = 0                                    
    for count in range (20):
        try:
            tweets.append(soup.findAll('div', {'class': 'js-tweet-text-container'})[count].find('p').text)
        except:
            break
    return tweets

def standardize_text(df, text_field):
    df[text_field] = df[text_field].str.replace(r"http\S+", "")
    df[text_field] = df[text_field].str.replace(r"http", "")
    df[text_field] = df[text_field].str.replace(r"@\S+", "")
    df[text_field] = df[text_field].str.replace(r"[^A-Za-z()!?@\_\n]", " ")
    df[text_field] = df[text_field].str.replace(r"@", "at")
    df[text_field] = df[text_field].str.lower()
    return df




def tokenize(text):
    stopWords = set(stopwords.words('english'))
    lemmatizer = WordNetLemmatizer()
    tokens = nltk.word_tokenize(text.lower())
    tokens = [lemmatizer.lemmatize(w) for w in tokens]
    for token in tokens:
        if token in stopWords:
            tokens.remove(token)
    return tokens
 
def tostr(twl):
    return ' '.join(el for el in twl)


link = 'https://twitter.com/katyperry'
blogger = link.split('/')[-1]
page = urlopen(link)
soup = BeautifulSoup(page,'html.parser')
tweets = {}
tweets[blogger] = get_tweets(link)
urls = links_from_acc(soup)
for url in tqdm(urls):    
    tweets[url.split('/')[-1]] = get_tweets(url)


df = pd.DataFrame(data={'blogger':list(tweets.keys()), 'tweets':list(tweets.values())})
df['tweets'] = df['tweets'].apply(tostr)
df = standardize_text(df, 'tweets')
df['tweet_token'] = df['tweets'].apply(tokenize)



con = psycopg2.connect(
    database='postgres',
    user='postgres',
    password='postgres',
    host='database-1-instance-1.ccerwsehleft.us-east-1.rds.amazonaws.com',
    port=5432,
)
con.autocommit = True
cur = con.cursor()




for i in range(len(df)):
    blogger = df['blogger'][i]
    words = []
    for wr in df['tweet_token'][i]:
        query = list()
        if (wr not in stop_list):
            query.append(wr)
        cur.execute("INSERT INTO tweets VALUES (%s)", query)
