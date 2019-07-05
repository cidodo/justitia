import React, { Component, Fragment } from 'react';
import { Icon, Select, Form, Input, Button, Upload, Radio, InputNumber } from 'antd';
import { connect } from 'dva';
import { toFile } from '@/utils/utils';

const FormItem = Form.Item;
const Option = Select.Option;
const TextArea = Input.TextArea;

@connect(({ ChannelManager }) => {
  return {
    ChannelManager
  };
})
class AddOrg extends Component {
  constructor(props) {
    super(props);
    this.state = {
      orgFileList: [],
      isTls: true,
    };
    this.downloadEle = React.createRef();
  }
  componentDidUpdate(prevProps) {
    const { isTls } = this.state;
    const { ChannelManager } = this.props;
    const { isAdd, memberFile } = ChannelManager;
    const prevAdd = prevProps.ChannelManager.isAdd;
    if (!isTls && !isAdd && prevAdd && memberFile) {
      const params = {
        ele: this.downloadEle.current,
        fileName: `不动产证书文件.zip`,
        object: memberFile
      };
      toFile(params);
    }
  }
  // 文件上传
  uploadChange = (info) => {
    let fileList = info.fileList;
    fileList = fileList.slice(-1);
    this.setState({
      orgFileList: fileList
    });
  }
  // 上传文件前的钩子函数
  // 返回false为手动上传文件
  beforeUpload = () => {
    return false;
  }
  // 判断是否存在证书
  handleChange = (e) => {
    this.setState({
      isTls: e.target.value
    })
  }
  // 提交表单
  handleSubmit = (e) => {
    e.preventDefault();
    const { form, dispatch } = this.props;
    const { isTls } = this.state;

    form.validateFields((err, values) => {
      delete values.tlsEnable;
      if (err) return false;
      let formData = new FormData();
      Object.keys(values).forEach(key => {
        formData.append(key, values[key].file ? values[key].file : values[key]);
      });

      const type = isTls ? "ChannelManager/handleAddOrg" : "ChannelManager/handleAddMember";
      const payload = isTls ? formData : values;

      dispatch({
        type,
        payload
      });
    });
  }

  render() {
    // Form表单layout
    const formItemLayout = {
      labelCol: {
        xs: { span: 24 },
        sm: { span: 8 },
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 8 },
      },
    };
    const { orgFileList, isTls } = this.state;
    const { form, ChannelManager, dataChannel } = this.props;
    const { isAdd } = ChannelManager;
    const { getFieldDecorator } = form;

    return (
      <Form onSubmit={this.handleSubmit}>
        <FormItem {...formItemLayout} hasFeedback={true} label={"所属通道"}>
          {getFieldDecorator('channelId', {
            rules: [
              {
                required: true,
                message: '通道不能为空！',
              }
            ],
          })(
            <Select placeholder="请选择想要加入的通道">
              {dataChannel.map(ele => (
                <Option key={ele.channelId} value={ele.channelId}>{ele.channelId}</Option>
              ))}
            </Select>
          )}
        </FormItem>
        <FormItem {...formItemLayout} hasFeedback={true} label={"组织名称"}>
          {getFieldDecorator('orgName', {
            rules: [{
              required: true,
              message: '请输入被增加组织的名称！',
            },
            {
              pattern: /^[a-zA-Z][0-9a-zA-Z]{1,128}$/g,
              message: ''
            }],
          })(
            <Input placeholder="被增加组织的名称！" />
          )}
        </FormItem>
        <FormItem {...formItemLayout} hasFeedback={true} label={"请求内容"}>
          {getFieldDecorator('description', {
            rules: [
              {
                required: true,
                message: '内容或原因说明不能为空！',
              }
            ],
          })(
            <TextArea row={6} />
          )}
        </FormItem>
        <FormItem {...formItemLayout} label={"是否存在证书"}>
          {getFieldDecorator('tlsEnable', {
            initialValue: isTls
          })(
            <Radio.Group onChange={this.handleChange}>
              <Radio value={true}>是</Radio>
              <Radio value={false}>否</Radio>
            </Radio.Group>
          )}
        </FormItem>
        {isTls ?
          <FormItem {...formItemLayout} label={"组织配置文件"}>
            {getFieldDecorator('orgConfig', {
              rules: [
                {
                  required: true,
                  message: '配置文件不能为空！',
                }
              ],
            })(
              <Upload
                name="cert"
                fileList={orgFileList}
                onChange={this.uploadChange}
                beforeUpload={this.beforeUpload}
              >
                <Button>
                  <Icon type="upload" /> 上传组织配置文件
                </Button>
              </Upload>
            )}
          </FormItem> :
          <Fragment>
            <FormItem {...formItemLayout} hasFeedback={true} label={"组织MSPID"}>
              {getFieldDecorator('orgMspId', {
                rules: [{
                  required: true,
                  message: '请输入被增加组织的MSPID！',
                },
                {
                  pattern: /^[a-zA-Z][0-9a-zA-Z]{1,128}$/g,
                  message: ''
                }],
              })(
                <Input placeholder="被输入组织MSPId！" />
              )}
            </FormItem>
            <FormItem {...formItemLayout} hasFeedback={true} label={"组织身份"}>
              {getFieldDecorator('identity', {
                rules: [
                  {
                    required: true,
                    message: '请选择组织身份！',
                  }
                ],
              })(
                <Select>
                  <Option value="land">land</Option>
                  <Option value="bank">bank</Option>
                  <Option value="agency">agency</Option>
                </Select>
              )}
            </FormItem>
            <FormItem {...formItemLayout} hasFeedback={true} label={"fabric用户数量"}>
              {getFieldDecorator('userCount', {
                initialValue: 3,
                rules: [
                  {
                    required: true,
                    message: '请输入fabric用户的数量！',
                  }, {
                    pattern: /^[0-9]{1,10}$/,
                    message: '请输入数字'
                  }
                ],
              })(
                <Input placeholder="fabric用户的数量！" />
              )}
            </FormItem>
            <FormItem {...formItemLayout} hasFeedback={true} label={"peer节点数量"}>
              {getFieldDecorator('peerCount', {
                initialValue: 3,
                rules: [
                  {
                    required: true,
                    message: '请输入peer节点的数量！',
                  }, {
                    pattern: /^[0-9]{1,10}$/,
                    message: '请输入数字'
                  }
                ],
              })(
                <Input placeholder="peer节点的数量！" />
              )}
            </FormItem>
          </Fragment>
        }
        <FormItem wrapperCol={{ span: 8, offset: 8 }}>
          <Button type="primary" block htmlType="submit" loading={isAdd}>确定</Button>
        </FormItem>
        <div ref={this.downloadEle}></div>
      </Form>
    )
  }
}

const AddOrgToChannel = Form.create()(AddOrg);
export default AddOrgToChannel;
