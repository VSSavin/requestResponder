mvn clean compile package

if ! [ -d $JavaDevelop_PATH/Projects/requestResponder/requestResponder ]; then
mkdir $JavaDevelop_PATH/Projects/requestResponder/requestResponder
fi
if ! [ -d $JavaDevelop_PATH/Projects/requestResponder/requestResponder/lib ]; then
mkdir $JavaDevelop_PATH/Projects/requestResponder/requestResponder/lib
fi

echo copy executeable jar to output project:
cp $JavaDevelop_PATH/Projects/requestResponder/target/*.jar $JavaDevelop_PATH/Projects/requestResponder/requestResponder/
echo copy libs to output project:
cp $JavaDevelop_PATH/Projects/requestResponder/lib/*.jar $JavaDevelop_PATH/Projects/requestResponder/requestResponder/lib/
echo copy setings
cp $JavaDevelop_PATH/Projects/requestResponder/settings.xml $JavaDevelop_PATH/Projects/requestResponder/requestResponder/
echo copy log4j
cp $JavaDevelop_PATH/Projects/requestResponder/log4j.xml $JavaDevelop_PATH/Projects/requestResponder/requestResponder/